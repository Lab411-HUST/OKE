/* 
 * 2010-2015 (C) Antonio Redondo
 * http://antonioredondo.com
 * https://github.com/AntonioRedondo/AnotherMonitor
 *
 * Code under the terms of the GNU General Public License v3.
 *
 */

package org.anothermonitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.*;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import javax.sql.DataSource;


public class ActivityMain extends Activity {
    static Thread th;
    static int m = 0;

    int n=80000;
    TextView tvv;
    boolean kill=true;
    long startTime, endTime;
    private boolean cpuTotal, cpuAM,
            memUsed, memAvailable, memFree, cached, threshold, canvasLocked;
    private int intervalRead, intervalUpdate, intervalWidth, animDuration = 200,
            settingsHeight, orientation, processesMode, graphicMode;
    //	private float sD;
    private SharedPreferences mPrefs;
    private FrameLayout mLSettings, mLGraphicSurface, mCloseSettings;
    private LinearLayout mLTopBar, mLProcessContainer,
            mLCPUTotal, mLCPUAM,
            mLMemUsed, mLMemAvailable, mLMemFree, mLCached, mLThreshold;
    private TextView mTVCPUTotalP, mTVCPUAMP, mTVMemoryAM,
            mTVMemTotal, mTVMemUsed, mTVMemAvailable, mTVMemFree, mTVCached, mTVThreshold,
            mTVMemUsedP, mTVMemAvailableP, mTVMemFreeP, mTVCachedP, mTVThresholdP;
    private ImageView mLButtonRecord/*, mIVSettingsBG*/;
    private DecimalFormat mFormat = new DecimalFormat("##,###,##0"), mFormatPercent = new DecimalFormat("##0.0"),
            mFormatTime = new DecimalFormat("0.#");
    private Resources res;
    private Button mBChooseProcess, mBMemory, mBRemoveAll;
    private ToggleButton mBHide;
    private ViewGraphic mVG;
    private SeekBar mSBRead;
    private ServiceReader mSR;
    private List<Map<String, Object>> mListSelected;
    private Intent tempIntent;
    private Handler mHandler = new Handler(), mHandlerVG = new Handler(), zipHanler = new Handler();
    private Thread mThread;
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath();
    private Runnable drawRunnable = new Runnable() {
        @SuppressWarnings("unchecked")
        @SuppressLint("NewApi")
        @Override
        public void run() {
//			long startTime=System.currentTimeMillis();

            mHandler.postDelayed(this, intervalUpdate);
            if (mSR != null) { // finish() could have been called from the BroadcastReceiver
                mHandlerVG.post(drawRunnableGraphic);

                setTextLabelCPU(null, mTVCPUTotalP, mSR.getCPUTotalP());
                if (processesMode == C.processModeCPU)
                    setTextLabelCPU(null, mTVCPUAMP, mSR.getCPUAMP());
                else setTextLabelCPU(null, mTVCPUAMP, null, mSR.getMemoryAM());
                setTextLabelMemory(mTVMemUsed, mTVMemUsedP, mSR.getMemUsed());
                setTextLabelMemory(mTVMemAvailable, mTVMemAvailableP, mSR.getMemAvailable());
                setTextLabelMemory(mTVMemFree, mTVMemFreeP, mSR.getMemFree());
                setTextLabelMemory(mTVCached, mTVCachedP, mSR.getCached());
                setTextLabelMemory(mTVThreshold, mTVThresholdP, mSR.getThreshold());

                for (int n = 0; n < mLProcessContainer.getChildCount(); ++n) {
                    LinearLayout l = (LinearLayout) mLProcessContainer.getChildAt(n);
                    setTextLabelCPUProcess(l);
                    setTextLabelMemoryProcesses(l);
                }

//				long endTime=System.currentTimeMillis();
//				long result=endTime-startTime;
//				try {
//					FileOutputStream file=new FileOutputStream((new File("/sdcard/AnotherMonitor/RecordResponseTime.txt")),true);
//					String tmp=String.valueOf(result);
//					file.write((tmp+"\n").getBytes());
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
            }
        }
    }, drawRunnableGraphic = new Runnable() { // http://stackoverflow.com/questions/18856376/android-why-cant-i-create-a-handler-in-new-thread
        @Override
        public void run() {
            mThread = new Thread() {
                @Override
                public void run() {
                    Canvas canvas = null;
                    if (!canvasLocked) { // http://stackoverflow.com/questions/9792446/android-java-lang-illegalargumentexception
                        canvas = mVG.lockCanvas();
                        if (canvas != null) {
                            canvasLocked = true;
                            mVG.onDrawCustomised(canvas, mThread);

                            // https://github.com/AntonioRedondo/AnotherMonitor/issues/1
                            // http://stackoverflow.com/questions/23893813/canvas-restore-causing-underflow-exception-in-very-rare-cases
                            try {
                                mVG.unlockCanvasAndPost(canvas);
                            } catch (IllegalStateException e) {
                                Log.w("Activity main: ", e.getMessage());
                            }
                            canvasLocked = false;
                        }
                    }
                }
            };

            mThread.start();
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @SuppressLint("NewApi")
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mSR = ((ServiceReader.ServiceReaderDataBinder) service).getService();

            mVG.setService(mSR);
            mVG.setParameters(cpuTotal, cpuAM, memUsed, memAvailable, memFree, cached, threshold);

            setIconRecording();

            mTVMemTotal.setText(mFormat.format(mSR.getMemTotal()) + C.kB);

            switchParameter(cpuTotal, mLCPUTotal);
            switchParameter(cpuAM, mLCPUAM);

            switchParameter(memUsed, mLMemUsed);
            switchParameter(memAvailable, mLMemAvailable);
            switchParameter(memFree, mLMemFree);
            switchParameter(cached, mLCached);
            switchParameter(threshold, mLThreshold);

            mHandler.removeCallbacks(drawRunnable);
            mHandler.post(drawRunnable);

            // When on ActivityProcesses the screen is rotated, ActivityMain is destroyed and back is pressed from ActivityProcesses
            // mSR isn't ready before onActivityResult() is called. So the Intent is saved till mSR is ready.
            if (tempIntent != null) {
                tempIntent.putExtra(C.screenRotated, true);
                onActivityResult(1, 1, tempIntent);
                tempIntent = null;
            } else onActivityResult(1, 1, null);
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mSR = null;
        }
    };

    private BroadcastReceiver receiverSetIconRecord = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setIconRecording();
        }
    }, receiverDeadProcess = new BroadcastReceiver() {
        @SuppressWarnings("unchecked")
        @Override
        public void onReceive(Context context, Intent intent) {
            switchParameterForProcess((Map<String, Object>) intent.getSerializableExtra(C.process));
        }
    }, receiverFinish = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };


    @SuppressWarnings("deprecation")
    @SuppressLint({"InlinedApi", "NewApi", "InflateParams"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, ServiceReader.class));
        setContentView(R.layout.activity_main);

//        zipHanler.postDelayed(zipRunnable, 0);

        mPrefs = getSharedPreferences(getString(R.string.app_name) + C.prefs, MODE_PRIVATE);
        readPrefs();
        res = getResources();
        orientation = res.getConfiguration().orientation;
        mLTopBar = (LinearLayout) findViewById(R.id.LTopBar);
        mVG = (ViewGraphic) findViewById(R.id.ANGraphic);
        final SeekBar mSBWidth = (SeekBar) findViewById(R.id.SBIntervalWidth);
        mLGraphicSurface = (FrameLayout) findViewById(R.id.LGraphicButton);


        mLButtonRecord = (ImageView) findViewById(R.id.LButtonRecord);
        mLButtonRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSR.isRecording())
                    mSR.stopRecord();
                else mSR.startRecord();
                mHandlerVG.post(drawRunnableGraphic);
            }
        });
        mLButtonRecord.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View arg0) {
                int id = R.string.menu_record_description;
                if (mSR.isRecording())
                    id = R.string.menu_record_stop_description;
                Toast.makeText(ActivityMain.this, getString(id), Toast.LENGTH_SHORT).show();
                return true;
            }
        });


        mLProcessContainer = (LinearLayout) findViewById(R.id.LProcessContainer);

        mLCPUTotal = (LinearLayout) findViewById(R.id.LCPUTotal);
        mLCPUTotal.setTag(C.cpuTotal);
        mLCPUTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchParameter(cpuTotal = !cpuTotal, mLCPUTotal);
            }
        });
        mLCPUAM = (LinearLayout) findViewById(R.id.LCPUAM);
        mLCPUAM.setTag(C.cpuAM);
        mLCPUAM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchParameter(cpuAM = !cpuAM, mLCPUAM);
            }
        });
        ((TextView) ((LinearLayout) mLCPUAM.getChildAt(2)).getChildAt(1)).setText("Pid: " + Process.myPid());

        mLMemUsed = (LinearLayout) findViewById(R.id.LMemUsed);
        mLMemUsed.setTag(C.memUsed);
        mLMemUsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchParameter(memUsed = !memUsed, mLMemUsed);
            }
        });
        mLMemAvailable = (LinearLayout) findViewById(R.id.LMemAvailable);
        mLMemAvailable.setTag(C.memAvailable);
        mLMemAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchParameter(memAvailable = !memAvailable, mLMemAvailable);
            }
        });
        mLMemFree = (LinearLayout) findViewById(R.id.LMemFree);
        mLMemFree.setTag(C.memFree);
        mLMemFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchParameter(memFree = !memFree, mLMemFree);
            }
        });
        mLCached = (LinearLayout) findViewById(R.id.LCached);
        mLCached.setTag(C.cached);
        mLCached.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchParameter(cached = !cached, mLCached);
            }
        });
        mLThreshold = (LinearLayout) findViewById(R.id.LThreshold);
        mLThreshold.setTag(C.threshold);
        mLThreshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchParameter(threshold = !threshold, mLThreshold);
            }
        });

        mTVCPUTotalP = (TextView) findViewById(R.id.TVCPUTotalP);
        mTVCPUAMP = (TextView) findViewById(R.id.TVCPUAMP);
        mTVMemoryAM = (TextView) findViewById(R.id.TVMemoryAM);
        mTVMemTotal = (TextView) findViewById(R.id.TVMemTotal);
        mTVMemUsed = (TextView) findViewById(R.id.TVMemUsed);
        mTVMemUsedP = (TextView) findViewById(R.id.TVMemUsedP);
        mTVMemAvailable = (TextView) findViewById(R.id.TVMemAvailable);
        mTVMemAvailableP = (TextView) findViewById(R.id.TVMemAvailableP);
        mTVMemFree = (TextView) findViewById(R.id.TVMemFree);
        mTVMemFreeP = (TextView) findViewById(R.id.TVMemFreeP);
        mTVCached = (TextView) findViewById(R.id.TVCached);
        mTVCachedP = (TextView) findViewById(R.id.TVCachedP);
        mTVThreshold = (TextView) findViewById(R.id.TVThreshold);
        mTVThresholdP = (TextView) findViewById(R.id.TVThresholdP);

        mLSettings = (FrameLayout) findViewById(R.id.LSettings);
        mLSettings.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mLSettings.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                settingsHeight = mLSettings.getHeight();
                mLSettings.getLayoutParams().height = 0;
            }
        });

        mLGraphicSurface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettings();
            }
        });
        mVG.setOpaque(false); // http://stackoverflow.com/questions/18993355/semi-transparent-textureviews-not-working

        mVG.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }

        });
        mVG.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mVG.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mLGraphicSurface.getLayoutParams();
                lp.setMargins((int) (mVG.getWidth() * 0.14), (int) (mVG.getHeight() * 0.1), (int) (mVG.getWidth() * 0.06), (int) (mVG.getHeight() * 0.12));
            }
        });

        mBChooseProcess = (Button) findViewById(R.id.BChooseProcess);
        mBChooseProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long startTime = System.currentTimeMillis();
                Intent i = new Intent(ActivityMain.this, ActivityProcesses.class);
                i.putExtra(C.listSelected, (Serializable) mListSelected);
                startActivityForResult(i, 1);
                long endTime = System.currentTimeMillis();
                long result = endTime - startTime;
                Log.v("myApp", "result:" + result);
                Toast.makeText(getApplication(), result + "", Toast.LENGTH_LONG).show();
            }
        });
//        mBChooseProcess.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                zipHanler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplication(),"Start measure response time!",Toast.LENGTH_SHORT).show();
//                        long timeStart = System.currentTimeMillis();
//                        actionZip();
//                        long timeEnd = System.currentTimeMillis();
//                        long result = timeEnd - timeStart;
//                        try {
//                            FileOutputStream fileResponse = new FileOutputStream(("sdcard/AnotherMonitor/Compress/CurrentResponseTime.txt"), true);
//                            String tmp = String.valueOf(result);
//                            fileResponse.write((tmp + ("\n")).getBytes());
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        File del = new File("sdcard/AnotherMonitor/Compress/Video.zip");
//                        boolean deleted = del.delete();
//                        Toast.makeText(getApplication(), "Done!", Toast.LENGTH_SHORT);
//                    }
//                }, 15000);
//                return false;
//            }
//        });
        mBMemory = (Button) findViewById(R.id.BMemory);
        mBMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processesMode = processesMode == C.processModeCPU ? C.processModeMemory : C.processModeCPU;
                mBMemory.setText(processesMode == 0 ? getString(R.string.w_main_memory) : getString(R.string.p_cpuusage));
                mVG.setProcessesMode(processesMode);
                mHandlerVG.post(drawRunnableGraphic);
                mHandler.removeCallbacks(drawRunnable);
                mHandler.post(drawRunnable);
            }
        });
        mBRemoveAll = (Button) findViewById(R.id.BRemoveAll);
        mBRemoveAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListSelected.clear(); // This also updates the List on ServiceReader because it is poiting to the same object
                mLProcessContainer.removeAllViews();
                mHandlerVG.post(drawRunnableGraphic);
                mBRemoveAll.animate().setDuration(300).alpha(0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mBRemoveAll.setVisibility(View.GONE);
                    }
                });
            }
        });
        mBHide = (ToggleButton) findViewById(R.id.BHide);
        mBHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graphicMode = graphicMode == C.graphicModeShow ? C.graphicModeHide : C.graphicModeShow;
                boolean checked = graphicMode == C.graphicModeShow ? false : true;
                mBHide.setChecked(checked);
                mVG.setGraphicMode(graphicMode);
                mHandlerVG.post(drawRunnableGraphic);
            }
        });

        final TextView mTVIntervalRead = (TextView) findViewById(R.id.TVIntervalRead);
        mTVIntervalRead.setText(getString(R.string.interval_read) + " " + mFormatTime.format(intervalRead / (float) 1000) + " s");
        final TextView mTVIntervalUpdate = (TextView) findViewById(R.id.TVIntervalUpdate);
        mTVIntervalUpdate.setText(getString(R.string.interval_update) + " " + mFormatTime.format(intervalUpdate / (float) 1000) + " s");
        final TextView mTVIntervalWidth = (TextView) findViewById(R.id.TVIntervalWidth);
        mTVIntervalWidth.setText(getString(R.string.interval_width) + " " + intervalWidth + " dp");

        mSBRead = (SeekBar) findViewById(R.id.SBIntervalRead);
        int t = 0;
        switch (intervalRead) {
            case 500:
                t = 0;
                break;
            case 1000:
                t = 1;
                break;
            case 2000:
                t = 2;
                break;
            case 4000:
                t = 4;
        }
        mSBRead.setProgress(t);
        mSBRead.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                int t = 0;
                switch (mSBRead.getProgress()) {
                    case 0:
                        t = 500;
                        break;
                    case 1:
                        t = 1000;
                        break;
                    case 2:
                        t = 2000;
                        break;
                    case 3:
                        t = 4000;
                }
                mTVIntervalRead.setText(getString(R.string.interval_read) + " " + mFormatTime.format(t / (float) 1000) + " s");
            }
        });

        final SeekBar mSBUpdate = (SeekBar) findViewById(R.id.SBIntervalUpdate);
        t = 0;
        switch (intervalUpdate) {
            case 500:
                t = 0;
                break;
            case 1000:
                t = 1;
                break;
            case 2000:
                t = 2;
                break;
            case 4000:
                t = 3;
        }
        mSBUpdate.setProgress(t);
        mSBUpdate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                int t = 0;
                switch (mSBUpdate.getProgress()) {
                    case 0:
                        t = 500;
                        break;
                    case 1:
                        t = 1000;
                        break;
                    case 2:
                        t = 2000;
                        break;
                    case 3:
                        t = 4000;
                }
                mTVIntervalUpdate.setText(getString(R.string.interval_update) + " " + mFormatTime.format(t / (float) 1000) + " s");
            }
        });


        t = 0;
        switch (intervalWidth) {
            case 1:
                t = 0;
                break;
            case 2:
                t = 1;
                break;
            case 5:
                t = 2;
                break;
            case 10:
                t = 4;
        }
        mSBWidth.setProgress(t);
        mSBWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                int t = 0;
                switch (mSBWidth.getProgress()) {
                    case 0:
                        t = 1;
                        break;
                    case 1:
                        t = 2;
                        break;
                    case 2:
                        t = 5;
                        break;
                    case 3:
                        t = 10;
                }
                mTVIntervalWidth.setText(getString(R.string.interval_width) + " " + t + " dp");
            }
        });

        mCloseSettings = (FrameLayout) findViewById(R.id.LOK);
        mCloseSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSettings();

                int intervalWidth = 0, intervalRead = 0, intervalUpdate = 0;

                switch (mSBRead.getProgress()) {
                    case 0:
                        intervalRead = 500;
                        break;
                    case 1:
                        intervalRead = 1000;
                        break;
                    case 2:
                        intervalRead = 2000;
                        break;
                    case 3:
                        intervalRead = 4000;
                }

                switch (mSBUpdate.getProgress()) {
                    case 0:
                        intervalUpdate = 500;
                        break;
                    case 1:
                        intervalUpdate = 1000;
                        break;
                    case 2:
                        intervalUpdate = 2000;
                        break;
                    case 3:
                        intervalUpdate = 4000;
                }

                switch (mSBWidth.getProgress()) {
                    case 0:
                        intervalWidth = 1;
                        break;
                    case 1:
                        intervalWidth = 2;
                        break;
                    case 2:
                        intervalWidth = 5;
                        break;
                    case 3:
                        intervalWidth = 10;
                }

                if (intervalRead > intervalUpdate) {
                    intervalUpdate = intervalRead;
                    int t = 0;
                    switch (intervalUpdate) {
                        case 500:
                            t = 0;
                            break;
                        case 1000:
                            t = 1;
                            break;
                        case 2000:
                            t = 2;
                            break;
                        case 4000:
                            t = 3;
                    }
                    mSBUpdate.setProgress(t);
                }

                if (ActivityMain.this.intervalRead != intervalRead) {
                    mSR.getCPUTotalP().clear();
                    mSR.getCPUAMP().clear();

                    if (mListSelected != null && !mListSelected.isEmpty())
                        for (Map<String, Object> process : mListSelected) {
                            process.put(C.pFinalValue, new ArrayList<Float>());
                            process.put(C.pTPD, new ArrayList<Integer>());
                        }

                    mSR.getMemUsed().clear();
                    mSR.getMemAvailable().clear();
                    mSR.getMemFree().clear();
                    mSR.getCached().clear();
                    mSR.getThreshold().clear();
                }

                ActivityMain.this.intervalRead = intervalRead;
                ActivityMain.this.intervalUpdate = intervalUpdate;
                ActivityMain.this.intervalWidth = intervalWidth;

                mSR.setIntervals(intervalRead, intervalUpdate, intervalWidth);
                mVG.calculateInnerVariables();
                mHandlerVG.post(drawRunnableGraphic);
                mHandler.removeCallbacks(drawRunnable);
                mHandler.post(drawRunnable);
                mPrefs.edit()
                        .putInt(C.intervalRead, intervalRead)
                        .putInt(C.intervalUpdate, intervalUpdate)
                        .putInt(C.intervalWidth, intervalWidth)
                        .commit();
            }
        });

        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            graphicMode = savedInstanceState.getInt(C.graphicMode);
            boolean checked = graphicMode == C.graphicModeShow ? false : true;
            mBHide.setChecked(checked);
            mVG.setGraphicMode(graphicMode);

            processesMode = savedInstanceState.getInt(C.processesMode);
            mBMemory.setText(processesMode == C.processModeCPU ? getString(R.string.w_main_memory) : getString(R.string.p_cpuusage));
            mVG.setProcessesMode(processesMode);

            canvasLocked = savedInstanceState.getBoolean(C.canvasLocked);
            if (savedInstanceState.getBoolean(C.menuShown))
                mLTopBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mLTopBar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//						mPWMenu.showAtLocation(mLTopBar, Gravity.TOP | Gravity.RIGHT, 0, mLTopBar.getHeight());
                    }
                });
        }
    }


    private void showSettings() {
        mLGraphicSurface.setEnabled(false);
        ValueAnimator va = ValueAnimator.ofInt(0, settingsHeight);
        va.setDuration(animDuration);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                mLSettings.getLayoutParams().height = value.intValue();
                mLSettings.requestLayout();
            }
        });
        va.start();
    }


    private void hideSettings() {
        mLGraphicSurface.setEnabled(true);
        ValueAnimator va = ValueAnimator.ofInt(settingsHeight, 0);
        va.setDuration(animDuration);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                mLSettings.getLayoutParams().height = value.intValue();
                mLSettings.requestLayout();
            }
        });
        va.start();
    }


    private void readPrefs() {
        intervalRead = mPrefs.getInt(C.intervalRead, 500);
        intervalUpdate = mPrefs.getInt(C.intervalUpdate, 1000);
        intervalWidth = mPrefs.getInt(C.intervalWidth, 5);

        cpuTotal = mPrefs.getBoolean(C.cpuTotal, true);
        cpuAM = mPrefs.getBoolean(C.cpuAM, true);

        memUsed = mPrefs.getBoolean(C.memUsed, true);
        memAvailable = mPrefs.getBoolean(C.memAvailable, true);
        memFree = mPrefs.getBoolean(C.memFree, false);
        cached = mPrefs.getBoolean(C.cached, false);
        threshold = mPrefs.getBoolean(C.threshold, true);
    }


    private void switchParameter(boolean draw, LinearLayout labelRow) {
        if (mSR == null)
            return;

        mPrefs.edit()
                .putBoolean(C.cpuTotal, cpuTotal)
                .putBoolean(C.cpuAM, cpuAM)

                .putBoolean(C.memUsed, memUsed)
                .putBoolean(C.memAvailable, memAvailable)
                .putBoolean(C.memFree, memFree)
                .putBoolean(C.cached, cached)
                .putBoolean(C.threshold, threshold)

                .commit();

        mVG.setParameters(cpuTotal, cpuAM, memUsed, memAvailable, memFree, cached, threshold);

        ImageView icon = (ImageView) labelRow.getChildAt(0);
        if (draw)
            icon.setImageResource(R.drawable.icon_play);
        else icon.setImageResource(R.drawable.icon_pause);

        mHandlerVG.post(drawRunnableGraphic);
    }


    @SuppressWarnings("unchecked")
    private void switchParameterForProcess(Map<String, Object> process) {
        LinearLayout l = null;
        for (int n = 0; n < mLProcessContainer.getChildCount(); ++n) {
            l = (LinearLayout) mLProcessContainer.getChildAt(n);
            if (((Map<String, Object>) l.getTag()).get(C.pId).equals(process.get(C.pId)))
                break;
        }
        ImageView iv = (ImageView) l.getChildAt(0);

        if (process.get(C.pDead) != null) {
            ((TextView) l.findViewById(R.id.TVpPercentage)).setText(getString(R.string.w_processes_dead));
            l.findViewById(R.id.TVpName).setAlpha(0.2f);
            l.findViewById(R.id.TVpAbsolute).setVisibility(View.INVISIBLE);
            l.getChildAt(1).setAlpha(0.3f);
        }

        if ((Boolean) process.get(C.pSelected)) {
            iv.setImageResource(R.drawable.icon_play);
            if (process.get(C.pDead) == null)
                setTextLabelCPUProcess(l);
        } else {
            iv.setImageResource(R.drawable.icon_pause);
        }

        mHandlerVG.post(drawRunnableGraphic);
    }


    private void setTextLabelCPU(TextView absolute, TextView percent, List<Float> values, @SuppressWarnings("unchecked") List<Integer>... valuesInteger) {
        if (valuesInteger.length == 1) {
            percent.setText(mFormatPercent.format(valuesInteger[0].get(0) * 100 / (float) mSR.getMemTotal()) + C.percent);
            mTVMemoryAM.setVisibility(View.VISIBLE);
            mTVMemoryAM.setText(mFormat.format(valuesInteger[0].get(0)) + C.kB);
        } else if (!values.isEmpty()) {
            percent.setText(mFormatPercent.format(values.get(0)) + C.percent);
            mTVMemoryAM.setVisibility(View.INVISIBLE);
        }
    }


    private void setTextLabelMemory(TextView absolute, TextView percent, List<String> values) {
        if (!values.isEmpty()) {
            absolute.setText(mFormat.format(Integer.parseInt(values.get(0))) + C.kB);
            percent.setText(mFormatPercent.format(Integer.parseInt(values.get(0)) * 100 / (float) mSR.getMemTotal()) + C.percent);
        }
    }


    @SuppressWarnings("unchecked")
    private void setTextLabelCPUProcess(LinearLayout l) {
        Map<String, Object> entry = (Map<String, Object>) l.getTag();
        if (entry != null
                && (List<String>) entry.get(C.pFinalValue) != null && ((List<String>) entry.get(C.pFinalValue)).size() != 0
                && (List<String>) entry.get(C.pTPD) != null && !((List<String>) entry.get(C.pTPD)).isEmpty()
                && entry.get(C.pDead) == null)
            if (processesMode == C.processModeCPU)
                ((TextView) l.findViewById(R.id.TVpPercentage)).setText(mFormatPercent.format(((List<String>) entry.get(C.pFinalValue)).get(0)) + C.percent);
            else
                ((TextView) l.findViewById(R.id.TVpPercentage)).setText(mFormatPercent.format(((List<Integer>) entry.get(C.pTPD)).get(0) * 100 / (float) mSR.getMemTotal()) + C.percent);
    }


    @SuppressWarnings("unchecked")
    private void setTextLabelMemoryProcesses(LinearLayout l) {
        TextView tv = (TextView) l.findViewById(R.id.TVpAbsolute);
        if (processesMode == C.processModeCPU)
            tv.setVisibility(View.INVISIBLE);
        else {
            Map<String, Object> entry = (Map<String, Object>) l.getTag();
            if (entry != null
                    && (List<String>) entry.get(C.pTPD) != null && !((List<String>) entry.get(C.pTPD)).isEmpty()
                    && entry.get(C.pDead) == null) {
                tv.setVisibility(View.VISIBLE);
                tv.setText(mFormat.format(((List<String>) entry.get(C.pTPD)).get(0)) + C.kB);
            }
        }
    }


    private void setIconRecording() {
        if (mSR == null) // This can happen when stopping and closing the app from the system bar action button.
            return;
        if (mSR.isRecording()) {
            mSBRead.setEnabled(false);
            mBChooseProcess.setEnabled(false);
            mLButtonRecord.setImageResource(R.drawable.button_stop_record);
        } else {
            mSBRead.setEnabled(true);
            mBChooseProcess.setEnabled(true);
            mLButtonRecord.setImageResource(R.drawable.button_start_record);
        }
    }


    private int getColourForProcess(int n) {
        if (n == 0)
            return res.getColor(R.color.process3);
        else if (n == 1)
            return res.getColor(R.color.process4);
        else if (n == 2)
            return res.getColor(R.color.process5);
        else if (n == 3)
            return res.getColor(R.color.process6);
        else if (n == 4)
            return res.getColor(R.color.process7);
        else if (n == 5)
            return res.getColor(R.color.process8);
        else if (n == 6)
            return res.getColor(R.color.process1);
        else if (n == 7)
            return res.getColor(R.color.process2);
        n -= 8;
        return getColourForProcess(n);
    }


    @SuppressLint({"NewApi", "InflateParams"})
    @SuppressWarnings("unchecked")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 1) {
            // List
            // Map
            // Integer	   C.pId
            // String	   C.pName
            // Integer	   C.work
            // Integer	   C.workBefore
            // List<Sring> C.finalValue
            // Boolean	   C.pDead

            // Boolean	   C.pCheckBox
            List<Map<String, Object>> mListSelectedProv = null;
            if (data != null) {
                mListSelectedProv = (List<Map<String, Object>>) data.getSerializableExtra(C.listSelected);
                if (mListSelectedProv == null)
                    return;

                // When on ActivityProcesses the screen is rotated, ActivityMain is destroyed and back is pressed from ActivityProcesses
                // mSR isn't ready before onActivityResult() is called. So the Intent is saved till mSR is ready.
                if (mSR == null) {
                    tempIntent = data;
                    return;
                }

                for (Map<String, Object> process : mListSelectedProv) {
                    process.put(C.pColour, getColourForProcess(mSR.getProcesses() != null ? mSR.getProcesses().size() : 0));
                    mSR.addProcess(process);
                }

                mListSelected = mSR.getProcesses();

                if (data.getBooleanExtra(C.screenRotated, false))
                    mListSelectedProv = mListSelected;

            } else {
                mListSelected = mSR.getProcesses();
                mListSelectedProv = mListSelected;
            }

            if (mListSelectedProv == null)
                return;

            mBRemoveAll.setAlpha(1);
            mBRemoveAll.setVisibility(View.VISIBLE);

            synchronized (mListSelected) {
                for (final Map<String, Object> process : mListSelectedProv) {
                    if (process.get(C.pSelected) == null)
                        process.put(C.pSelected, Boolean.TRUE);

                    final LinearLayout l = (LinearLayout) getLayoutInflater().inflate(R.layout.layer_process_entry, null);
                    l.setTag(process);
                    l.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (!mSR.isRecording()) {
                                mSR.removeProcess(process);
                                mListSelected.remove(process);
                                mLProcessContainer.removeView(l);
                                return true;
                            } else return false;
                        }
                    });
                    l.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Boolean b = (Boolean) process.get(C.pSelected);
                            process.put(C.pSelected, !b);
                            switchParameterForProcess(process);

                        }
                    });

                    Drawable d = null;
                    try {
                        d = getPackageManager().getApplicationIcon((String) process.get(C.pPackage));
                    } catch (NameNotFoundException e) {
                    }

                    ImageView pIcon = (ImageView) l.getChildAt(1);
                    pIcon.setImageDrawable(d);

                    int colour = (Integer) process.get(C.pColour);

                    TextView pName = (TextView) l.findViewById(R.id.TVpAppName);
                    pName.setText((String) process.get(C.pAppName));
                    pName.setTextColor(colour);

                    TextView pId = (TextView) l.findViewById(R.id.TVpName);
                    pId.setText("Pid: " + process.get(C.pId));

                    TextView pUsage = (TextView) l.findViewById(R.id.TVpPercentage);
                    pUsage.setTextColor(colour);

                    mLProcessContainer.addView(l);
                    switchParameterForProcess(process);
                }
            }
        }

//		orientationChanged = false;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(C.graphicMode, graphicMode);
        outState.putInt(C.processesMode, processesMode);
        outState.putInt(C.orientation, orientation);
//		outState.putBoolean(C.menuShown, mPWMenu.isShowing());
//		outState.putBoolean(C.settingsShown, settingsShown);
        outState.putBoolean(C.canvasLocked, canvasLocked);
    }


    @Override
    public void onStart() {
        super.onStart();
        bindService(new Intent(this, ServiceReader.class), mServiceConnection, 0);
        registerReceiver(receiverSetIconRecord, new IntentFilter(C.actionSetIconRecord));
        registerReceiver(receiverDeadProcess, new IntentFilter(C.actionDeadProcess));
        registerReceiver(receiverFinish, new IntentFilter(C.actionFinishActivity));
    }


    @Override
    public void onResume() {
        super.onResume();
        mHandler.removeCallbacks(drawRunnable);
        mHandler.post(drawRunnable);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
        mHandler.removeCallbacks(drawRunnable);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
        mHandler.removeCallbacks(drawRunnable);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//		orientationChanged = false;
        kill = false;
        super.onDestroy();
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
        mHandler.removeCallbacks(drawRunnable);
        unregisterReceiver(receiverSetIconRecord);
        unregisterReceiver(receiverDeadProcess);
        unregisterReceiver(receiverFinish);
        unbindService(mServiceConnection);
    }

//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {
//            return true;
//        }
//        return super.onKeyUp(keyCode, event);
//    }



//    */using 2 code zip files bellow*/
public void actionZip() {
    try{
        Toast.makeText(getApplicationContext(), "Measuring...",
                Toast.LENGTH_LONG).show();
        LinkedList<File>
                files=DirTraversal.listLinkedFiles(path+"/AnotherMonitor/Compress/files");
        File
                fileZip=DirTraversal.getFilePath(path+"/AnotherMonitor/Compress/ ","compress_files.zip");
        ZipUtils.zipFiles(files, fileZip);
    }catch(IOException e){
        e.printStackTrace();
    }
    }

//    public void actionZip() {
//        byte[] buffer = new byte[1024];
//        try {
//            FileOutputStream fos = new FileOutputStream(
//                    "/sdcard/AnotherMonitor/Compress/Video.zip");
//            ZipOutputStream zos = new ZipOutputStream(fos);
//            ZipEntry ze = new ZipEntry("bcs.avi");
//            zos.putNextEntry(ze);
//            FileInputStream in = new FileInputStream(
//                    "/sdcard/AnotherMonitor/Compress/bcs.avi");
//            int len;
//            int count = 0;
//
//            while (count < 5) {
//                count++;
//                if (ze.isDirectory()) {
//
//                    continue;
//                }
//                while ((len = in.read(buffer)) > 0) {
//                    zos.write(buffer, 0, len);
//                }
//                in.close();
//                zos.closeEntry();
//
//                zos.close();
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }

    //    public void actionUnZip() {
//        Toast.makeText(getApplicationContext(), "giai nen", Toast.LENGTH_LONG)
//                .show();
//        File file = DirTraversal.getFilePath(
//                path + "/AnotherMonitor/Compress/", "Video.zip");
//        try {
//            ZipUtils.upZipFile(file, path + "/AnotherMonitor/Compress");
//        } catch (ZipException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    public void actionZip(){
//        String h264Path = "path to my h264 file, generated by Android MediaCodec";
//
//        DataSource videoFile = new FileDataSourceImpl(h264Path);
//
//        H264TrackImpl h264Track = new H264TrackImpl(videoFile, "eng", 5, 1); // 5fps. you can play with timescale and timetick to get non integer fps, 23.967 is 24000/1001
//
//        Movie movie = new Movie();
//
//        movie.addTrack(h264Track);
//
//        Container out = new DefaultMp4Builder().build(movie);
//        FileOutputStream fos = new FileOutputStream(new File("path to my generated file.mp4"));
//        out.writeContainer(fos.getChannel());
//
//        fos.close();
//        }

    //zip
//    public void startMeasureResponseTime(){
//        zipHanler.postDelayed(zipRunnable,0);
//    }
//
//    public Runnable zipRunnable = new Runnable() {
//        @Override
//        public void run() {
////            File del = new File("sdcard/AnotherMonitor/Compress/Video.zip");
////            del.delete();
//            long timeStart = System.currentTimeMillis();
//            actionZip();
//            long timeEnd = System.currentTimeMillis();
//            long result = timeEnd - timeStart;
//            try {
//                FileOutputStream fileResponse = new FileOutputStream(("sdcard/AnotherMonitor/Compress/CurrentResponseTime.txt"), true);
//                String tmp = String.valueOf(result);
//                fileResponse.write((tmp + ("\n")).getBytes());
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
////            actionUnZip();
//            zipHanler.postDelayed(this, 30000);
//        }
//    };
    Handler hd = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            long t =  (Long) msg.obj;
            //tvv.setText("Respone Time: "+String.valueOf(t)+ " milisecond");
            super.handleMessage(msg);
        }


    };
    public void sapxepMang() throws InterruptedException {

        th = new Thread() {

            @Override
            public void run() {
                super.run();
                while (kill) {
                    m++;

                    int[] a = new int[n];
                    Random rand = new Random();

                    /* for(int i=0; i<n; i++) */// System.out.printf("\t" + a[i]);
                    for (int i = 0; i < n; i++)
                        switch (a[i] = rand.nextInt()) {
                        }
                    //	System.out.println("\n");
                    long startTime = System.currentTimeMillis();

                    for (int j = 0; j < a.length; j++) {
                        int max = a[j];
                        int index = j;
                        for (int k = j + 1; k < a.length; k++) {
                            if (max < a[k]) {
                                max = a[k];
                                index = k;
                            }
                        }
                        // Nếu chỉ số đã thay đổi, ta sẽ hoán vị
                        if (index != j) {
                            int temp = a[j];
                            a[j] = a[index];
                            a[index] = temp;
                        }
                    }

                    //	System.out.println("\nSap xep:");
                    for (int h = 0; h < n; h++) {
                        //		System.out.printf("\t" + a[h]);
                    }
                    //	System.out.println("\n");

                    long endTime = System.currentTimeMillis();
                    long rt = endTime - startTime;

                    Message mess = new Message();
                    mess.obj = rt;
                    hd.sendMessage(mess);
                    System.out.println("thoi gian:" + rt);

                    try {
                        FileOutputStream file = new FileOutputStream((new File("/sdcard/ResponeTime.txt")),true);
                        String tmp=String.valueOf(rt);
                        file.write((tmp+"\n").getBytes());
                    } catch (FileNotFoundException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
					/*if (m == 60)
						break;*/

                }
            }
        };
        th.start();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.startMeasure:

                try {
                    sapxepMang();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
//            case R.id.stopMeasure:
//                zipHanler.removeCallbacks(sapxepMang(););
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
