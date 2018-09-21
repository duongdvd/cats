package jp.co.willwave.aca.handler;

import com.google.common.base.Throwables;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@ControllerAdvice
public class GlobalExceptionHandler implements HandlerExceptionResolver {
    private static Logger logger = Logger.getLogger(GlobalExceptionHandler.class);

    protected final MessageSource messageSource;

    @Autowired
    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object object,
                                         Exception ex) {
        logger.error(ex.getMessage(), ex);

        ModelAndView mav = new ModelAndView();

        mav.addObject("errorMessage", Throwables.getRootCause(ex));

        mav.setViewName("error/error");
        return mav;
    }
}
