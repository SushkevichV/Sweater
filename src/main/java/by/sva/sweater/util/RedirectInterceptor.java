package by.sva.sweater.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/* при использовании turbolink в при переходе по ссылке до полной загрузки и обновления страницы
 * возможен некорректный перевод, т.к. страница кешируется
 * Поэтому turdolink нужно уведомлять о редиректах.
 */

public class RedirectInterceptor extends HandlerInterceptorAdapter {
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if(modelAndView != null) {
			String args = request.getQueryString() != null ? "?" + request.getQueryString() : ""; // получить аргументы url после знака ?
			String url = request.getRequestURI().toString() + args;
			response.setHeader("Turbolinks-Location", url);
		}
	}
}
