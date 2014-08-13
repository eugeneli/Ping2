package com.ping.util;

public class HtmlBuilder
{	
	public static String buildImageHtml(String url)
	{
		String html = "<html style='background-color:#000000'><body><center>";
		html += "<img src=\""+ url + "\" />";
		html += "</center></body></html>";
		
		return html;
	}
}
