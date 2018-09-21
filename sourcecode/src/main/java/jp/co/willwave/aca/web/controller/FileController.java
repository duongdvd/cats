package jp.co.willwave.aca.web.controller;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.utilities.FileUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Controller
public class FileController extends AbstractController {
	private final FileUtil fileUtil;

	public FileController(FileUtil fileUtil) {
		this.fileUtil = fileUtil;
	}

	@GetMapping(value = "/icon/{key}")
	public void downloadIcon(@PathVariable("key") String key, HttpServletResponse response) throws CommonException {
		fileUtil.downloadFileFromKey(key, response);
	}
}
