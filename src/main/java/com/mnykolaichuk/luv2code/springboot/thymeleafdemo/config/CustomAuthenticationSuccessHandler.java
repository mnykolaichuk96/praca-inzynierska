package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.config;


import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Employee;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Workshop;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.EmployeeService;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.WorkshopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private WorkshopService workshopService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		String redirectUrl = null; 		//створюємо урл по яких буде перекеровуватися юзер а по яких автосервіс після успішного логування
		String username = authentication.getName();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		for(GrantedAuthority grantedAuthority : authorities) {
			if (grantedAuthority.getAuthority().equals("ROLE_EMPLOYEE")) {
				redirectUrl = "/employee/dashboard";
				Employee tempEmployee = employeeService.findByUsername(username);

				// now place in the session
				HttpSession session = request.getSession();
				session.setAttribute("employee", tempEmployee);

				break;
			} else if (grantedAuthority.getAuthority().equals("ROLE_WORKSHOP")) {
				redirectUrl = "/workshop/dashboard";
				Workshop tempWorkshop = workshopService.findByUsername(username);

				// now place in the session
				HttpSession session = request.getSession();
				session.setAttribute("workshop", tempWorkshop);
				break;
			} else if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
				redirectUrl = "/admin/dashboard";
				Employee employee = employeeService.findByUsername(username);

				// now place in the session
				HttpSession session = request.getSession();
				session.setAttribute("admin", employee);
				break;
			}

		}
		//forward to page page for role
		response.sendRedirect(request.getContextPath() + redirectUrl);
		}
}
