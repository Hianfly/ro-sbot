package com.hiandev.rosbot.scanner;

import java.awt.AWTException;

public class InfoScanner extends TextScanner {
	
	public InfoScanner(int _x, int _y) throws AWTException {
		super (_x, _y, 220, 56);
		setAssetsDir("./assets/text-info/");
		setTextPixels(new int[][] { { 0, 0, 0 }, { 250, 0, 0 } });
		setInterval(100);
	}

	private int    lastLv    = 0;
	private String lastJob   = "";
	private int    lastJobLv = 0;
	private float  lastExp   = 0f;
	private int    lastHp    = 0;
	private int    lastHpMax = 0;
	private int    lastSp    = 0;
	private int    lastSpMax = 0;
	
	@Override
	public void onTextChanged(String[] rowTexts) {
		super.onTextChanged(rowTexts);
		int    lv    = 0;
		String job   = "";
		int    jobLv = 0;
		float  exp   = 0f;
		int    hp    = 0;
		int    hpMax = 0;
		int    sp    = 0;
		int    spMax = 0;
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
				sp    = Integer.parseInt(e[6]);
				spMax = Integer.parseInt(e[8]);
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
		System.out.println("Lvl:" + lv  + "[ " + exp + "% ]   "
				         + "Job:" + job + "[ " + jobLv + " ]   "
				         +  "HP:" + hp  + "/" + hpMax + "[ " + (hp * 100 / hpMax) + "% ]   "
				         +  "SP:" + sp  + "/" + spMax + "[ " + (sp * 100 / spMax) + "% ]   "
				         );
	}
	
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
	
}
