package jp.co.willwave.aca.service;

import jp.co.willwave.aca.utilities.CatsMessageResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseService {
    protected Logger logger;

    @Autowired
    protected CatsMessageResource messageSource;

    public BaseService() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    public interface ServiceCaller {
        Object execute() throws Exception;
    }

}
