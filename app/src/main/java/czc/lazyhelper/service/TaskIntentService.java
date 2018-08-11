package czc.lazyhelper.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ZipUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import czc.lazyhelper.constant.Const;
import czc.lazyhelper.manager.ParseManager;
import czc.lazyhelper.manager.PathManager;

public class TaskIntentService extends IntentService {

    public TaskIntentService() {
        super("task_intent_service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        if (action.equals(Const.ACTION_COPY_ASSET)) {
            PathManager.init();
            copyAssetRes();
            ParseManager.getInstance().readTaskConfig();
        }
    }

    private void copyAssetRes() {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = getAssets().open("phone.db");

            String phoneDbPath = PathManager.getInstance().getPhoneDbPath();
            File phoneDb = new File(phoneDbPath);

            byte[] buffer = new byte[0];
            int byteCount;
            if (!phoneDb.exists()) {
                fos = new FileOutputStream(phoneDbPath);
                buffer = new byte[1024];
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
            }

            is = getAssets().open("config/task.zip");
            String zipPath = PathManager.getInstance().getConfigZipPath();
            File zip = new File(zipPath);
            if (!zip.exists()) {
                fos = new FileOutputStream(zipPath);
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
            }
            ZipUtils.unzipFile(zipPath, PathManager.getInstance().getConfigDir());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d("destroy");
    }
}
