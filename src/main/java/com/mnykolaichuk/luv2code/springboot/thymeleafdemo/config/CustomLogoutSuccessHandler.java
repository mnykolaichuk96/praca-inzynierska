//package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.config;
//
//
//import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Employee;
//import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Workshop;
//import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.EmployeeService;
//import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.WorkshopService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.io.IOException;
//import java.util.Collection;
//
//@Component
//public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
//
//	@Autowired
//	private EmployeeService employeeService;
//
//	@Autowired
//	private WorkshopService workshopService;
//
//	@Override
//	public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
//		String redirectUrl = "home";        //створюємо урл по яких буде перекеровуватися юзер
//		// а по яких автосервіс після успішного логування
//		HttpSession session = request.getSession();
//		response.sendRedirect(request.getContextPath() + redirectUrl);
//	}
//
//	@Override
//	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
//			throws IOException, ServletException {
//
//
//	}
//}
