package Team4.Booksys.controller;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import Team4.Booksys.VO.EventVO;
import Team4.Booksys.VO.ReservationVO;
import Team4.Booksys.VO.modefiedEvent;
import Team4.Booksys.VO.modefiedReservationDivideDateAndTime;
import Team4.Booksys.service.EventService;
import Team4.Booksys.service.ReservationRepository;
import Team4.Booksys.service.ReservationService;
import Team4.Booksys.service.TableService;
import Team4.Booksys.service.UserRepository;

@Controller
public class ReservationController {
	@Autowired
	ReservationService ReservationService;
	@Autowired
	EventService EventService;
	@Autowired
	TableService TableService;
	@Autowired
	UserRepository userRepository;

	@ResponseBody
	@RequestMapping(value = "/cancleReservation.do")
	public String cancleReservation(HttpServletRequest request, Model model) {
		int cancleoid = Integer.parseInt(request.getParameter("cancleoid"));
		ReservationVO vo = ReservationService.findByOid(cancleoid); //취소할 예약의 vo
		ReservationService.removeReservationForUser(vo);
		return "<script> alert('취소 완료');  location.href= '/showUserReservation'; </script>";
	}
	
	@RequestMapping(value = "/callDeleteReserve/{oid}", produces = "text/html; charset=UTF-8")
	public String callDeleteReserve(@PathVariable int oid,HttpSession session) {
		ReservationVO vo = ReservationService.findByOid(oid);
		ReservationService.removeReservationForUser(vo);
		return "redirect:/showUserReservation";			
	}
	
	@Autowired
	ReservationRepository ReservationRepository;

	/* 아래 걸로 수정헀습니다.
	@RequestMapping(value = "/callModifyReserve/{oid}", produces = "text/html; charset=UTF-8")
	public String modifyReserve(@PathVariable int oid,HttpSession session,Model model) { //아직 작업중인 코드 leewk
		model.addAttribute("userid", session.getAttribute("id"));
		model.addAttribute("reserveOid", oid);
		
		ReservationVO vo=ReservationRepository.findByOid(oid);
		modefiedReservationDivideDateAndTime mReservdt = new modefiedReservationDivideDateAndTime();
		mReservdt.setVal_oid(vo.getVal_oid());
		mReservdt.setVal_people_number(vo.getVal_people_number());
		mReservdt.setVal_rank(vo.getVal_rank());
		mReservdt.setVal_start_time(vo.getVal_start_time());
		mReservdt.setVal_tid(vo.getVal_tid());

		EventVO evo = EventService.findbyRid(oid);
		modefiedEvent mEvent = new modefiedEvent();
		if(evo != null) {
			mEvent.setVal_oid(evo.getVal_oid());
			mEvent.setVal_rid(evo.getVal_rid());
			mEvent.setVal_event_type(evo.getVal_event_type());
			mEvent.setVal_event_song(evo.getVal_event_song());
			mEvent.setVal_event_memo(evo.getVal_event_memo());
		}
		
		model.addAttribute("vo",vo.getVal_start_time());
		model.addAttribute("mReserv",mReservdt);
		model.addAttribute("mEvent",mEvent);
		//mEvent.oid가 -1이면 이벤트예약이 존재하지 않음을 의미함
		
		return "modifyReserve";
		
	}
	*/
	/*
	@RequestMapping(value="/modifyReserve")
	public String modify() {
		return "modifyReserve";
	}
	*/
	
	@RequestMapping(value = "/callModifyReserve")
	public String modifyReserve(HttpSession session,Model model,ServletRequest req) { //아직 작업중인 코드 leewk
		
		/*수정부분- juhee*/
		if(req.getParameter("oid").contentEquals("")) {
			return "redirect:/showUserReservation";
		}
		int oid=Integer.parseInt(req.getParameter("oid"));
		System.out.print("oid"+oid);
		if(session.getAttribute("id") == null)return "/index";
		
		model.addAttribute("reserveOid", oid);
		model.addAttribute("userid", session.getAttribute("id"));
		
		
		ReservationVO vo=ReservationRepository.findByOid(oid);
		if(!session.getAttribute("id").equals(userRepository.findByUid(vo.getVal_uid()).getVal_id()))return "/index";

		modefiedReservationDivideDateAndTime mReservdt = new modefiedReservationDivideDateAndTime();
		mReservdt.setVal_oid(vo.getVal_oid());
		mReservdt.setVal_people_number(vo.getVal_people_number());
		mReservdt.setVal_rank(vo.getVal_rank());
		mReservdt.setVal_start_time(vo.getVal_start_time());
		mReservdt.setVal_tid(vo.getVal_tid());

		EventVO evo = EventService.findbyRid(oid);
		modefiedEvent mEvent = new modefiedEvent();
		if(evo != null) {
			mEvent.setVal_oid(evo.getVal_oid());
			mEvent.setVal_rid(evo.getVal_rid());
			mEvent.setVal_event_type(evo.getVal_event_type());
			mEvent.setVal_event_song(evo.getVal_event_song());
			mEvent.setVal_event_memo(evo.getVal_event_memo());
		}
		
		model.addAttribute("vo",vo.getVal_start_time());
		model.addAttribute("mReserv",mReservdt);
		model.addAttribute("mEvent",mEvent);
		//mEvent.oid가 -1이면 이벤트예약이 존재하지 않음을 의미함
		
		return "modifyReserve";
	}
	
	
	@ResponseBody
	@RequestMapping(value = "/modifyReservation.do")
	public String showTableView(HttpServletRequest request, Model model, HttpSession session) {
		//클라에서 주는 값들
		//reseravationoid
		//eventoid <-없으면 -1
		//date
		//time
		//num_people
		//
		//이벤트있으면
		//type
		//song
		//memo
		//
		if(session.getAttribute("id") == null)return "/index";

		int reserv_oid = Integer.parseInt(request.getParameter("reservationoid"));
		ReservationVO cur_reserv = ReservationService.findByOid(reserv_oid);



		String input_date = request.getParameter("date");// 날짜 가져오기
		String input_time = request.getParameter("time");// 시간 가져오기
		String input_datetime = input_date + " " + input_time;
		int input_num_people = Integer.parseInt(request.getParameter("num_people"));
		
		
		String origin_datetime =cur_reserv.getVal_start_time(); 
		if(origin_datetime.equals(input_datetime)) {//시간의 변동이 없다면 예약대기 조절할 필요가 없음 변경되는 값은 오직 인원수뿐
			ReservationService.updateReservationPeopleNumber(input_num_people, reserv_oid);
		}
		else { //시간의 변동이 있다면 변경이전 시간에 있던 예약들의 rank를 재조정해야함
			
			// 자동배정의 tid 구하기
			int new_tid = 1;
			int mintablewait = 9999;
			int numOftable = TableService.numberofTable(); // 테이블의개수
			int new_wait = -1;
			int new_rank = -1;
			for (int j = 1; j <= numOftable; j++) {
				int i = ReservationService.findWaitRank(input_datetime, j);
				if (i < mintablewait) {
					mintablewait = i;
					new_tid = j;
				}
			}
			
			int i = ReservationService.findWaitRank(input_datetime, new_tid);// 동일날짜 동시간대에 있는 예약의 개수 리턴
			if (i != 0)/* 이미 해당시간에 예약이 존재한다면 */ {
				new_wait = 1;// 예약이 존재한다.
				new_rank = i;// 대기순서는 i
			} else/* 해당시간에 예약이 없다면 */ {
				new_wait = 0;
				new_rank = 0;
			}
			
			ReservationService.modifyReservation(reserv_oid, input_datetime, input_num_people, new_tid, new_wait, new_rank);
		} 	
		////여기까지 하면 예약수정 완료함
		////여기서부터 이벤트 수정부분임
		int event_oid = Integer.parseInt(request.getParameter("eventoid"));
		
		if(event_oid != -1) {//이벤트가 존재한다면
			String event_type = request.getParameter("type");
			String event_song = request.getParameter("song");
			String event_memo = request.getParameter("memo");
			EventService.modifyEvent(event_type, event_song, event_memo, reserv_oid);
			
		}
		//이벤트수정부분 끝
		
		
		model.addAttribute("userid", session.getAttribute("id"));	
		return "<script> alert('수정 완료');  location.href= '/showUserReservation'; </script>";
	}
}
