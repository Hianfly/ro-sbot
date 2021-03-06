package com.hiandev.rosbot.scanner.text.info;

import java.awt.AWTException;

import com.hiandev.rosbot.GlobalVar;
import com.hiandev.rosbot.scanner.text.TextScanner;

public class InfoScanner extends TextScanner {
	
	public InfoScanner(int _x, int _y) throws AWTException {
		super (_x, _y, 220, 56);
		setAssetsDir("./assets/text-info/");
		setTextPixels(new int[][] { { 0, 0, 0 } });
		setInterval(10);
		setDelay(1123);
	}
	@Override
	public void onTextChanged(String[] rowTexts) {
		super.onTextChanged(rowTexts);
		try {
//			if (GlobalVar.getGameState() != GlobalVar.GAME_STATE_BATTLE) {
//				return;
//			}
			lv    = 0;
			job   = "";
			jobLv = 0;
			exp   = 0f;
			hp    = 0;
			hpMax = 0;
			sp    = 0;
			spMax = 0;
			for (String rowText : rowTexts) {
				String[] e = rowText.trim().split(" ");
				if (e[0].startsWith("Lv.")) {
					lv    = Integer.parseInt(e[0].replace("Lv.", ""));
					job   = e[2];
					jobLv = Integer.parseInt(e[4].replace("Lv.", ""));
					exp   = Float.parseFloat(e[7]);
				}
				if (e[0].startsWith("HP.")) {
					hp    = Integer.parseInt(e[1]);
					hpMax = Integer.parseInt(e[3]);
					try {
						sp    = Integer.parseInt(e[6]);
						spMax = Integer.parseInt(e[8]);
					} catch (Exception ex) {
						sp    = 1;
						spMax = 1;
					}
				}
			}
			if (lv != lastLv) {
				onLvChanged(lastLv, lv);
			}
			if (job != lastJob) {
				onJobChanged(lastJob, job);
			}
			if (jobLv != lastJobLv) {
				onJobLvChanged(lastJobLv, jobLv);
			}
			if (exp != lastExp) {
				onExpChanged(lastExp, exp);
			}
			if (hp != lastHp) {
				onHpChanged(lastHp, hp, lastHpMax, hpMax);
			}
			if (sp != lastSp) {
				onSpChanged(lastSp, sp, lastSpMax, spMax);
			}
			lastLv    = lv;
			lastJob   = job;
			lastJobLv = jobLv;
			lastExp   = exp;
			lastHp    = hp;
			lastHpMax = hpMax;
			lastSp    = sp;
			lastSpMax = spMax;
			if (isDebug()) {
				System.out.println("Lvl:" + lv  + "[ " + exp + "% ]   "
						         + "Job:" + job + "[ " + jobLv + " ]   "
						         +  "HP:" + hp  + "/" + hpMax + "[ " + (hpMax == 0 ? "?" : (hp * 100 / hpMax) + "") + "% ]   "
						         +  "SP:" + sp  + "/" + spMax + "[ " + (spMax == 0 ? "?" : (sp * 100 / spMax) + "") + "% ]   "
						         );
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int    lv    = 0;
	private String job   = "";
	private int    jobLv = 0;
	private float  exp   = 0f;
	private int    hp    = 0;
	private int    hpMax = 0;
	private int    sp    = 0;
	private int    spMax = 0;
	
	private int    lastLv    = 0;
	private String lastJob   = "";
	private int    lastJobLv = 0;
	private float  lastExp   = 0f;
	private int    lastHp    = 0;
	private int    lastHpMax = 0;
	private int    lastSp    = 0;
	private int    lastSpMax = 0;
	
	protected void onLvChanged(int oldLv, int newLv) {
		
	}
	protected void onJobChanged(String oldJob, String newJob) {
		
	}
	protected void onJobLvChanged(int oldJobLv, int newJobLv) {
		
	}
	protected void onExpChanged(float oldExp, float newExp) {
		
	}
	protected void onHpChanged(int oldHp, int newHp, int oldHpMax, int newHpMax) {
		
	}
	protected void onSpChanged(int oldSp, int newSp, int oldSpMax, int newSpMax) {
		
	}
	
	public int getHp() {
		return hp;
	}
	public int getHpMax() {
		return hpMax;
	}
	public int getSp() {
		return sp;
	}
	public int getSpMax() {
		return spMax;
	}
	
}
