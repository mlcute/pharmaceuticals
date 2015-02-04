package com.mobcast.util;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import com.sanofi.in.mobcast.AnnounceDBAdapter;
import com.sanofi.in.mobcast.AnnounceListView;
import com.sanofi.in.mobcast.R;

public class DeleteFile {
	String tableName;
	String _id;
	Context ctx;
	
	public DeleteFile(Context ctx,String tableName, String _id)
	{
		this._id = _id;
		this.tableName = tableName;
		this.ctx = ctx;
	}
	
public	int Delete() {
		System.out.println("deletefile called");
		try {
			AnnounceDBAdapter adb = new AnnounceDBAdapter(ctx);
			adb.open();
			Cursor c = adb.getrow(tableName, _id);
			System.out.println("delete cursor has "+c.getCount()+" elements");
			String name = c.getString((c
					.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_NAME)));
			String type = c.getString((c
					.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_TYPE)));
			String root = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			File myDir = null;
			
			//fetching the directory
			if (type.contentEquals("image")) {
				myDir = new File(root + "/.mobcast/mobcast_images");
				myDir.mkdirs();
			}
			else if (type.contentEquals("audio")){
				myDir = new File(root + "/.mobcast/mobcast_audio");
				myDir.mkdirs();
			}
			else if (type.contentEquals("video")){
				myDir = new File(root + "/.mobcast/mobcast_videos");
				myDir.mkdirs();
			}
			
			else if (type.contentEquals("pdf")){
				myDir = new File(Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ "/.mobcast/"
						+ ctx.getString(R.string.pdf)
						+ "/");
				myDir.mkdirs();
				name = c.getString((c
						.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_ENCRYPTION)));
			}
			else if (type.contentEquals("ppt")){
				myDir = new File(Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ "/.mobcast/"
						+ ctx.getString(R.string.ppt)
						+ "/");
				myDir.mkdirs();
				name = c.getString((c
						.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_ENCRYPTION)));
			}
			else if (type.contentEquals("doc")){
				myDir = new File(Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ "/.mobcast/"
						+ ctx.getString(R.string.doc)
						+ "/");
				myDir.mkdirs();
				name = c.getString((c
						.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_ENCRYPTION)));
			}
			else if (type.contentEquals("xls")){
				myDir = new File(Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ "/.mobcast/"
						+ ctx.getString(R.string.xls)
						+ "/");
				myDir.mkdirs();
				name = c.getString((c
						.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_ENCRYPTION)));
			}
			
			
			//Deleting the file
			try {
				String fname = name;
				File file = new File(myDir, fname);
				Log.d("deleting file", file.getAbsolutePath());
				file.delete();
				return 1;
			} catch (Exception e) {e.printStackTrace();
			}
			
			File file1 = new File(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/.mobcast/temp/");
			DeleteRecursive(file1);
			
			
			
		} catch (Exception e) {e.printStackTrace();
		}

		return 0;
	}

void DeleteRecursive(File fileOrDirectory) {
    if (fileOrDirectory.isDirectory())
        for (File child : fileOrDirectory.listFiles())
            DeleteRecursive(child);

    fileOrDirectory.delete();
}

}
