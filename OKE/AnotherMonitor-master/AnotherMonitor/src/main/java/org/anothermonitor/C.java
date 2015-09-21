/*
 * 2010-2015 (C) Antonio Redondo
 * http://antonioredondo.com
 * https://github.com/AntonioRedondo/AnotherMonitor
 *
 * Code under the terms of the GNU General Public License v3.
 *
 */

package org.anothermonitor;

class C {

	static final String prefs = "Prefs";
	static final String sbh = "status_bar_height";
	static final String nbh = "navigation_bar_height";
	static final String dimen = "dimen";
	static final String android = "android";

	// ServiceReader
	static final String readThread = "readThread";
	
	static final String actionStartRecord = "actionRecord";
	static final String actionStopRecord = "actionStop";
	static final String actionClose = "actionClose";
	static final String actionSetIconRecord = "actionSetIconRecord";
	static final String actionDeadProcess = "actionRemoveProcess";
	static final String actionFinishActivity = "actionCloseActivity";

	static final String pId = "pId";
	static final String pName = "pName";
	static final String pPackage = "pPackage";
	static final String pAppName = "pAppName";
	static final String pTPD = "pPTD";
	static final String pSelected = "pSelected";
	static final String pDead = "pDead";
	static final String pColour = "pColour";
	static final String work = "work";
	static final String workBefore = "workBefore";
	static final String pFinalValue = "finalValue";
	static final String process = "process";
	static final String screenRotated = "screenRotated";
	static final String listSelected = "listSelected";
	static final String listProcesses = "listProcesses";
	
	// ActivityMain
	static final String kB = "kB";
	static final String percent = "%";
	static final String menuShown = "menuShown";
	static final String settingsShown = "settingsShown";
	static final String orientation = "orientation";
	static final String processesMode = "processesMode";
	static final String graphicMode = "graphicMode";
	static final String canvasLocked = "canvasLocked";
	
	static final String intervalRead = "intervalRead";
	static final String intervalUpdate = "intervalUpdate";
	static final String intervalWidth = "intervalWidth";
	
	static final String cpuTotal = "cpuTotalD";
	static final String cpuAM = "cpuAMD";
	static final String memUsed = "memUsedD";
	static final String memAvailable = "memAvailableD";
	static final String memFree = "memFreeD";
	static final String cached = "cachedD";
	static final String threshold = "thresholdD";
	
	// GraphicView
	static final int processModeCPU = 0;
	static final int processModeMemory = 1;
	static final int graphicModeShow = 0;
	static final int graphicModeHide = 1;
	
	// ActivityPreferences
	static final String currentItem = "ci";
	
	static final String mSRead = "mSRead";
	static final String mSUpdate = "mSUpdate";
	static final String mSWidth = "mSWidth";
	
	static final String mCBMemFreeD = "memFreeD";
	static final String mCBBuffersD = "buffersD";
	static final String mCBCachedD = "cachedD";
	static final String mCBActiveD = "activeD";
	static final String mCBInactiveD = "inactiveD";
	static final String mCBSwapTotalD = "swapTotalD";
	static final String mCBDirtyD = "dirtyD";
	static final String mCBCpuTotalD = "cpuTotalD";
	static final String mCBCpuAMD = "cpuAMD";
}
