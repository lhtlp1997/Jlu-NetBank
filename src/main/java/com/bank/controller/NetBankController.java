package com.bank.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import aj.org.objectweb.asm.Label;

import com.bank.model.Record;
import com.bank.model.SubCredit;
import com.bank.model.User;
import com.bank.repository.ECardRepository;
import com.bank.repository.RecordRepository;
import com.bank.util.Constant;

@SuppressWarnings("unused")
@Controller
public class NetBankController {

	private final ECardRepository ecard;
	private final RecordRepository rec;

	@Autowired
	public NetBankController(ECardRepository ecard, RecordRepository rec) {
		this.ecard = ecard;
		this.rec = rec;
	}

	@GetMapping("/information.html")
	public String netbankGet(Model model, HttpServletRequest request, final RedirectAttributes attributes) {
		User user = (User) request.getSession().getAttribute("currentUser");
		model.addAttribute("currentUser", user);

		if (user == null) {
			attributes.addFlashAttribute("message", "使用网上银行，需要先进行登录！");
			return "redirect:/" + "login.html";
		}
		if(user.geteCard() == null){
			attributes.addFlashAttribute("message", "您账户下没有一卡通，需要注册才可以使用网上银行！");
			return "redirect:/" + "index.html";
		}
		model.addAttribute("ecardnumber", user.geteCard().getAccountName());

		// 利息
		model.addAttribute("depositrate", Constant.currentDepositRate);
		model.addAttribute("fixedrate", Constant.fixedDepositRate);
		model.addAttribute("fixanddeposit", Constant.fixAndDepositRate);

		// 子账户放进去
		Set<SubCredit> subCredits = user.geteCard().getSubCredits();
		model.addAttribute("subCredits", subCredits);

		// 记录放进去
		Set<Record> records = user.geteCard().getRecords();

		model.addAttribute("records", records);

		return "information";
	}

	@PostMapping("/search")
	public String timeSearch(@RequestParam String range, final RedirectAttributes attributes,
			HttpServletRequest request, Model model) {
		String dateString1 = range.substring(0, 10);
		String dateString2 = range.substring(13, 23);

		Set<Record> newRecords = new HashSet<>();

		// 获取用户信息
		User user = (User) request.getSession().getAttribute("currentUser");
		Set<Record> records = user.geteCard().getRecords();

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		/********************** 这一部分没写好，GET请求将所有的内容再重复传一遍 *******************/

		model.addAttribute("ecardnumber", user.geteCard().getAccountName());

		// 利息
		model.addAttribute("depositrate", Constant.currentDepositRate);
		model.addAttribute("fixedrate", Constant.fixedDepositRate);
		model.addAttribute("fixanddeposit", Constant.fixAndDepositRate);

		// 子账户放进去
		Set<SubCredit> subCredits = user.geteCard().getSubCredits();
		model.addAttribute("subCredits", subCredits);

		/***************************************************************************/

		try {
			Date dateStart = format.parse(dateString1);
			Date dateEnd = format.parse(dateString2);
			for (Record r : records) {
				if (r.getOccurDate().before(dateEnd) && r.getOccurDate().after(dateStart)) {
					newRecords.add(r);
				}
			}
			model.addAttribute("records", newRecords);
		} catch (ParseException e) {
			attributes.addFlashAttribute("message", "输入的信息不合法");
			return "redirect:/" + "information.html";
		}
		return "information";
	}

	@GetMapping("/data")
	@ResponseBody
	public HashMap<String, Object> getData(HttpSession session) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		User user = (User) session.getAttribute("currentUser");
		HashMap<String, Object> map = new HashMap<>();
		Double depositMoney = 0.0;
		Double drawMoney = 0.0;
		Double transferMoney = 0.0;
		Double rateMoney = 0.0;
		Double loanGive = 0.0;
		Double loanBack = 0.0;

		List<Record> records = rec.findAll();
		if (records.size() > 15) {
			records = records.subList(records.size() - 15, records.size());

		}
		List<Double> totalMoney = new ArrayList<Double>();
		List<String> totalDate = new ArrayList<>();

		// 获取这个账户的所有交易信息
		for (Record record : records) {
			if(record.geteCard().getUser().getIdNum().equals(user.getIdNum())){
				totalMoney.add(record.getMoney());
				totalDate.add(format.format(record.getOccurDate()));
			}
		}

		// 所有交易的钱
		for (Record record:records) {
			if(record.geteCard().getUser().getIdNum().equals(user.getIdNum())){
				if(record.getType() == 1){
					depositMoney += record.getMoney();
				}
				if(record.getType() == 2){
					drawMoney += record.getMoney();
				}
				if(record.getType() == 3){
					transferMoney += record.getMoney();
				}
				if(record.getType() == 4){
					rateMoney += record.getMoney();
				}
				if(record.getType() == 5){
					loanBack += record.getMoney();
				}
				if(record.getType() == 6){
					loanGive += record.getMoney();
				}
			}
		}

		map.put("line", totalMoney);
		map.put("row", totalDate);
		
		map.put("deposit", depositMoney);
		map.put("draw", drawMoney);
		map.put("transfer", transferMoney);
		map.put("rate", rateMoney);
		map.put("back", loanBack);
		map.put("give", loanGive);

		return map;
	}

}
