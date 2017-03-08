//package com.hiandev.rosbot.event;
//
//import com.hiandev.rosbot.scanner.HpSpScanner;
//
//public class HpSpEvent extends Event<HpSpScanner> {
//	
//	public HpSpEvent(HpSpScanner scanner) {
//		super (scanner);
//	}
//	
//	private int lastHpPercentage = 100;
//	
//	@Override
//	public void execute() {
//		int hpPercentage = getScanner().getHpPercentage();
//		if (hpPercentage != lastHpPercentage) {
//			onHpChanged(lastHpPercentage = hpPercentage);
//		}
//	}
//    
//	public void onHpChanged(int percentage) {
//		
//	}
//	
//}
