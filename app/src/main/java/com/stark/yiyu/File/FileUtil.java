package com.stark.yiyu.File;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.stark.yiyu.Format.FileType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Stark on 2017/7/4.
 */
public class FileUtil {
    public static String getSuffix(String name){
        for(int i=name.length()-1;i>0;i--){
            if(name.substring(i,i+1).equals(".")){
                return name.substring(i,name.length());
            }
        }
        return "";
    }
    public static String getName(String name) {
        for(int i=name.length()-1;i>0;i--){
            if(name.substring(i,i+1).equals(".")){
                return name.substring(0,i);
            }
        }
        return "";
    }
    public static String getUsefulPath(String oldPath,String hashCode,String oldName){
        File dir = new File(oldPath);
        if(dir.isDirectory()){
            if(!dir.exists()){
                dir.mkdirs();
            }
            oldPath += "/" + hashCode + FileUtil.getSuffix(oldName);
        }else{
            if(dir.exists()) {
                int i = 1;
                while (( new File(dir.getParentFile().getPath()  + "/" + FileUtil.getName(oldName) + "(" + i + ")" + FileUtil.getSuffix(oldName))).exists()) {
                    i++;
                }
                oldPath = dir.getParentFile().getPath()  + "/" + FileUtil.getName(oldName) + "(" + i + ")" + FileUtil.getSuffix(oldName);
            }
        }
        return oldPath;
    }
    public static String getPath(FileType type){
        String path=Environment.getExternalStorageDirectory()+"/campus";
        switch (type){
            case ImgTemp:
                path+="/temp";
                break;
            case Head:
                path+="/head";
                break;
            case mHead:
                path+="/head/mhead";
                break;
            case oHead:
                path+="/head/ohead";
                break;
        }
        File f=new File(path);
        if(!f.exists()){
            f.mkdirs();
        }
        return path;
    }
    public static boolean Copy(String srcFileName, String destFileName, boolean overlay) {
        File srcFile = new File(srcFileName);
        if (!srcFile.exists()) {
            return false;
        } else if (!srcFile.isFile()) {
            return false;
        }
        File destFile = new File(destFileName);
        if (destFile.exists()) {
            if (overlay) {
                new File(destFileName).delete();
            }
        } else {
            if (!destFile.getParentFile().exists()) {
                if (!destFile.getParentFile().mkdirs()) {
                    return false;
                }
            }
        }
        int n;
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];

            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static boolean CopyDirectory(String srcDirName, String destDirName, boolean overlay) {
        File srcDir = new File(srcDirName);
        if (!srcDir.exists()) {
            return false;
        } else if (!srcDir.isDirectory()) {
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        File destDir = new File(destDirName);
        if (destDir.exists()) {
            if (overlay) {
                DeleteFile(destDir);
            } else {
                return false;
            }
        } else {
            if (!destDir.mkdirs()) {
                return false;
            }
        }
        boolean flag = true;
        File[] files = srcDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 复制文件
            if (files[i].isFile()) {
                flag = Copy(files[i].getAbsolutePath(),
                        destDirName + files[i].getName(), overlay);
                if (!flag)
                    break;
            } else if (files[i].isDirectory()) {
                flag =Copy(files[i].getAbsolutePath(),
                        destDirName + files[i].getName(), overlay);
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            return false;
        } else {
            return true;
        }
    }
    public static boolean Move(String path,String desdir,boolean overlay){
        try {
            File file = new File(path);
            File dfile=new File(desdir + "/" + file.getName());
            if(dfile.exists()){
                if(overlay) {
                    DeleteFile(dfile);
                }else{
                    return false;
                }
            }
            if (file.renameTo(dfile)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static Uri PathToUri(String path){
        DeleteFileOrDir(new File(path));
        return Uri.fromFile(new File(path));
    }
    public static void DeleteFileOrDir(File file) {
        if (file.isFile()) {
            Log.e("delete",""+ DeleteFile(file));
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                DeleteFile(file);
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                DeleteFileOrDir(childFiles[i]);
            }
            DeleteFile(file);
        }
    }
    public static boolean DeleteFile(File file) {
        if (file != null) {
            String tmpPath = file.getParent() + File.separator + System.currentTimeMillis();
            File tmp = new File(tmpPath);
            file.renameTo(tmp);
            return tmp.delete();
        }
        return false;
    }
    public static String getPhotoPathFromContentUri(Context context, Uri uri) {
        String photoPath = "";
        if(context == null || uri == null) {
            return photoPath;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if(isExternalStorageDocument(uri)) {
                String [] split = docId.split(":");
                if(split.length >= 2) {
                    String type = split[0];
                    if("primary".equalsIgnoreCase(type)) {
                        photoPath = Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
            }
            else if(isDownloadsDocument(uri)) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                photoPath = getDataColumn(context, contentUri, null, null);
            }
            else if(isMediaDocument(uri)) {
                String[] split = docId.split(":");
                if(split.length >= 2) {
                    String type = split[0];
                    Uri contentUris = null;
                    if("image".equals(type)) {
                        contentUris = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    }
                    else if("video".equals(type)) {
                        contentUris = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    }
                    else if("audio".equals(type)) {
                        contentUris = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    String selection = MediaStore.Images.Media._ID + "=?";
                    String[] selectionArgs = new String[] { split[1] };
                    photoPath = getDataColumn(context, contentUris, selection, selectionArgs);
                }
            }
        }
        else if("file".equalsIgnoreCase(uri.getScheme())) {
            photoPath = uri.getPath();
        }
        else {
            photoPath = getDataColumn(context, uri, null, null);
        }

        return photoPath;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return null;
    }
}
