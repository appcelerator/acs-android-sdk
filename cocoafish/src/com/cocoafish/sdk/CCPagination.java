package com.cocoafish.sdk;

import org.json.JSONException;
import org.json.JSONObject;

public class CCPagination {
	private int page; 	// current page
	private int perPage; // per page count
	private int totalPages; // total pages can be returned
	private int totalResults; // total number of results
	
	public int getPage() {
		return page;
	}
	
	public int getPerPage() {
		return perPage;
	}
	
	public int getTotalPages() {
		return totalPages;
	}
	
	public int getTotalResults() {
		return totalResults;
	}

	public CCPagination(JSONObject jObject) throws CocoafishError {
		try {
			page = Integer.parseInt(jObject.getString("page").trim());
		} catch (NumberFormatException e) {
			throw new CocoafishError("Invalid Server Response: CCPagination: page should be a number");
		} catch (JSONException e) {
			throw new CocoafishError("Invalid Server Response: CCPagination: missing page");
		}
		
		try {
			perPage =  Integer.parseInt(jObject.getString("per_page").trim());
		} catch (NumberFormatException e) {
			throw new CocoafishError("Invalid Server Response: CCPagination: perPage is not a number");

		} catch (JSONException e) {
			throw new CocoafishError("Invalid Server Response: CCPagination: missing perPage");
		}
		
		try {
			totalPages = Integer.parseInt(jObject.getString("total_pages").trim());
		} catch (NumberFormatException e) {
			throw new CocoafishError("Invalid Server Response: CCPagination: totoalPages is not a number");
		} catch (JSONException e) {
			throw new CocoafishError("Invalid Server Response: CCPagination: missing totalPages");
		}
		
		try {
			totalResults = Integer.parseInt(jObject.getString("total_results").trim());
		} catch (NumberFormatException e) {
			throw new CocoafishError("Invalid Server Response: CCPagination: totalResults is not a number");
		} catch (JSONException e) {
			throw new CocoafishError("Invalid Server Response: CCPagination: missing totalResults");

		}
	}
}