package com.techelevator.fbn.controller;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.techelevator.fbn.model.DollarAmount;
import com.techelevator.fbn.model.Stock;

@Controller
@RequestMapping("/fbn")
@SessionAttributes("stocks")
public class StocksController {
	@RequestMapping(path={"/stocks"}, method=RequestMethod.GET)
	public String showStocksPage(ModelMap modelHolder){
		List<Stock> stocks = getStocks();
		
		modelHolder.put("stocks", stocks);
		
		return "fbn/stocksLandingPage";
	}
	
	@RequestMapping(path={"/stockPurchase"}, method=RequestMethod.GET)
	public String buyStockForm(){
		return "fbn/stocksPurchasePage";
	}
	
	@RequestMapping(path={"/stockPurchase"}, method=RequestMethod.POST)
	public String buyStock(@RequestParam String ticker, @RequestParam int shares){
		System.out.println("You bought " + shares + " shares of " + ticker + "!");
		return "redirect:/fbn/stocks";
	}
	
	private List<Stock> getStocks() {
		List<Stock> stocks = new ArrayList<Stock>();
		String queryUrl = "http://download.finance.yahoo.com/d/quotes.csv?s=AAPL+GOOG+MSFT+CSCO+TSLA+NFLX&f=snabc1";

		try {
			URLConnection connection = new URL(queryUrl).openConnection();
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			InputStream response = connection.getInputStream();
			
			Scanner input = new Scanner(response);
			while(input.hasNextLine()) {
				Stock stock = new Stock();
				
				String line = input.nextLine();
				String[] nameParts = line.split("\"");
				
				stock.setTicker(nameParts[1]);
				stock.setName(nameParts[3]);
				
				String[] parts = nameParts[4].split(",");
				
				if(!parts[1].equals("N/A")) {
					stock.setPrice(new BigDecimal(parts[1]));
				}
				
				if(!parts[3].equals("N/A")) {
					float change = Float.parseFloat(parts[3]);
					stock.setChange(change);
				}
				
				stocks.add(stock);
			}
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return stocks;
	}
}
