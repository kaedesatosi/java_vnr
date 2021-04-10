package app.service;

import app.Global;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseTranslateService {

	public void start() {
		Global.translateService = this;
		log.info("{}启动", getServiceName());
	}

	public void stop() {
		Global.translateService = null;
		log.info("{}关闭", getServiceName());
	}

	public abstract String getServiceName();

	public abstract String translate(String text);

}
