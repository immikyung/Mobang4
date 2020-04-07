package com.finalproject.mobang.login.controller;

import java.util.Locale;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.finalproject.mobang.login.biz.LoginBiz;
import com.finalproject.mobang.login.biz.LoginBizImpl;
import com.finalproject.mobang.login.dto.LoginDto;
import com.finalproject.mobang.login.email.MailHandler;
import com.finalproject.mobang.user.biz.roomsearchBiz;
import com.finalproject.mobang.user.controller.HomeController;

@Controller
public class LoginController {

private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	LoginBiz biz;
	
	/* Email */
	@Autowired
	MailHandler mailService = new MailHandler();
	
	@RequestMapping(value = "/login.all")
	public String home(Locale locale, Model model) {
		logger.info("login");

		return "login/login";
	}
	
	@RequestMapping(value = "/access_denied_page.all")
	public String access_denied_page(Locale locale, Model model) {
		logger.info("access_denied_page");

		return "login/access_denied_page";
	}
	
	@RequestMapping(value = "/usersignupform.all")
	public String usersignupform(Locale locale, Model model) {
		logger.info("usersignupform");
		
		model.addAttribute("loginDto", new LoginDto());
		
		return "login/user_signup";
	}
	
	@RequestMapping(value = "/usersignup.all")
	public String usersignup(Model model, @ModelAttribute("loginDto")@Valid LoginDto loginDto, BindingResult result,
			RedirectAttributes rttr) {
		logger.info("usersignup");
		
		model.addAttribute("dto", new LoginDto());
		
		System.out.println(loginDto.getRoommate());
		
		if(result.hasErrors()) {
			return "login/user_signup";
		} else {
			
			if(loginDto.getRoommate() != null) {
				System.out.println(loginDto.getPwd());
				
				loginDto.setPwd("{noop}"+loginDto.getPwd());
				
				System.out.println(loginDto.getPwd());
				
				int res = biz.userInsert(loginDto);
				
				if(res > 0) {
					return "login/login";
				} else {
					return "login/user_signup";
				}
			} else {
				System.out.println(loginDto.getPwd());
				
				loginDto.setPwd("{noop}"+loginDto.getPwd());
				loginDto.setClean("");
				loginDto.setLifestyle("");
				loginDto.setShower("");
				loginDto.setFavoriteage("");
				loginDto.setGender("");
				loginDto.setAnimal("");
				loginDto.setNeeds("");
				
				System.out.println(loginDto.getPwd());
				
				int res = biz.userInsert(loginDto);
				
				if(res > 0) {
					return "login/login";
				} else {
					return "login/user_signup";
				}
			}
		}
		
	}
	
	@RequestMapping(value = "/agentsignupform.all")
	public String agentsignupform(Locale locale, Model model) {
		logger.info("agentsignupform");
		
		model.addAttribute("loginDto", new LoginDto());
		
		return "login/agent_signup";
	}
	
	@RequestMapping(value = "/agentsignup.all")
	public String agentsignup(Model model, @ModelAttribute("loginDto")@Valid LoginDto loginDto, BindingResult result,
			RedirectAttributes rttr) {
		logger.info("agentsignup");
		
		model.addAttribute("dto", new LoginDto());
		
		if(result.hasErrors()) {
			return "login/agent_signup";
		} else {
			loginDto.setPwd("{noop}"+loginDto.getPwd());
			int res = biz.agentInsert(loginDto);
				
			if(res > 0) {
				return "login/login";
			} else {
				return "login/agent_signup";
			}
		}
	}
	
	@RequestMapping(value = "/userupdateform.all")
	public String userupdateform(Locale locale, Model model) {
		logger.info("userupdateform");
		
		LoginDto dto = biz.selectUser(currentUserName());
		
		model.addAttribute("loginDto", dto);
		
		System.out.println(dto.getEmail());
		
		
		return "login/user_update";
	}
	
	@RequestMapping(value = "/userupdate.all")
	public String userupdate(Model model, @ModelAttribute("loginDto")@Valid LoginDto loginDto, BindingResult result,
			RedirectAttributes rttr) {
		logger.info("userupdate");	
		
		System.out.println("userupdate : "+ loginDto);
		
		if(result.hasErrors()) {
			return "login/user_update";
		} else {
			loginDto.setPwd("{noop}"+loginDto.getPwd());

			System.out.println("controller : "+ loginDto);
			int res = biz.userUpdate(loginDto);
				
			if(res > 0) {
				return "index";
			} else {
				return "login/user_update";
			}
		}
	}
	
	@RequestMapping(value = "/agentupdateform.all")
	public String agentupdateform(Locale locale, Model model) {
		logger.info("agentupdateform");
		
		model.addAttribute("loginDto", biz.selectUser(currentUserName()));
		
		return "login/agent_update";
	}
	
	@RequestMapping(value = "/agentupdate.all")
	public String agentupdate(Model model, @ModelAttribute("loginDto")@Valid LoginDto loginDto, BindingResult result,
			RedirectAttributes rttr) {
		logger.info("agentupdate");
		
		if(result.hasErrors()) {
			return "login/agent_update";
		} else {
			loginDto.setPwd("{noop}"+loginDto.getPwd());
			int res = biz.agentUpdate(loginDto);
				
			if(res > 0) {
				return "/";
			} else {
				return "login/agent_update";
			}
		}
	}
	
	@RequestMapping(value="/email")
	public class emailController {
	
		@RequestMapping(value="/email.do")
		public ModelAndView board2(){
			ModelAndView mv = new ModelAndView();
			int ran = new Random().nextInt(900000) + 100000;
			mv.setViewName("test/email");
			mv.addObject("random", ran);
			return mv;
		}
	}
	
	@RequestMapping(value="/createEmailCheck.all")
		@ResponseBody
		public boolean createEmailCheck(@RequestParam String email, @RequestParam int random, HttpServletRequest req){
			//이메일 인증
			int ran = new Random().nextInt(900000) + 100000;
			
			
			HttpSession session = req.getSession(true);
			String authCode = String.valueOf(ran);
			
			session.setAttribute("authCode", authCode);
			session.setAttribute("random", random);
			
			String subject = "회원가입 인증 코드 발급 안내 입니다.";
			StringBuilder sb = new StringBuilder();
			
			sb.append("귀하의 인증 코드는 " + authCode + "입니다.");
			
			return mailService.send(subject, sb.toString(), "hyerin03158@gmail.com", email);
	}

	@RequestMapping(value="/emailAuth.all")
	@ResponseBody
	public ResponseEntity<String> emailAuth(@RequestParam String authCode, @RequestParam String random, HttpSession session){
		String originalJoinCode = (String) session.getAttribute("authCode");
		String originalRandom = Integer.toString((int) session.getAttribute("random"));
		
		if(originalJoinCode.equals(authCode) && originalRandom.equals(random)) {
			return new ResponseEntity<String>("complete", HttpStatus.OK);
		}
		else {
			return new ResponseEntity<String>("false", HttpStatus.OK);
		}
	}
	
	public static String currentUserName() { 
	      Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); 
	      User user = (User) authentication.getPrincipal();
	      
	      return user.getUsername(); 
	}
	
}
