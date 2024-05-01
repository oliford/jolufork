package uk.co.oliford.jolu;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class ScientificNumberFormat extends NumberFormat {
	private static final long serialVersionUID = -6018936529882969268L;
	
	private DecimalFormat normalFmt, exponentFmt;    	
	private double tooBig, tooSmall;
	
	public ScientificNumberFormat() {
		normalFmt = new DecimalFormat("#.#####");
    	exponentFmt = new DecimalFormat("#.#####E0");
    	tooBig = 1e5;
    	tooSmall = 1.0 / tooBig;
	}
	
	public ScientificNumberFormat(String normalFmt, String exponentFmt) {
		this.normalFmt = new DecimalFormat(normalFmt);
    	this.exponentFmt = new DecimalFormat(exponentFmt);
    	tooBig = 7e4;
    	tooSmall = 3.0 / tooBig;
	}

	public ScientificNumberFormat(String normalFmt, String exponentFmt, int maxDecimalPlaces) {
		this.normalFmt = new DecimalFormat(normalFmt);
    	this.exponentFmt = new DecimalFormat(exponentFmt);
    	tooBig = Math.pow(10, maxDecimalPlaces);        	
    	tooSmall = 1.0 / tooBig;
    	tooBig *= 0.7;
    	tooSmall *= 3;
	}
	@Override
	public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
		if(Double.isNaN(number))
			return new StringBuffer("NaN");
		
		return (number < -tooBig || number > tooBig || (number > -tooSmall && number < tooSmall))
				? exponentFmt.format(number, toAppendTo, pos)
				: normalFmt.format(number, toAppendTo, pos);					
	}
	
	@Override
	public StringBuffer format(long number, StringBuffer toAppendTo,
			FieldPosition pos) {
		
		return (number < -tooBig || number > tooBig || (number > -tooSmall && number < tooSmall))
				? exponentFmt.format(number, toAppendTo, pos)
				: normalFmt.format(number, toAppendTo, pos);
	}
	
	@Override
	public Number parse(String source, ParsePosition parsePosition) {
		return  normalFmt.parse(source, parsePosition);
	}
	
}
