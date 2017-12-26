package com.leafchild.scopequotas.common;

import android.os.Environment;
import android.util.Log;
import com.leafchild.scopequotas.data.Quota;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * @author victor
 * @date 12/25/17
 */

public class ExportUtils {

	private ExportUtils() {}

	public static boolean exportData(String[] headers, List<Quota> quotes) throws IOException {

		boolean result = true;
		CSVWriter writer = null;
		File exportFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
			"scopes_quotas_" + quotes.get(0).getQuotaType().name() + "_"
				+ System.currentTimeMillis() + ".csv");

		try {
			if (exportFile.createNewFile()) {

				writer = new CSVWriter(new FileWriter(exportFile));
				writer.writeNext(headers);

				for (Quota quota : quotes) {
					writer.writeNext(quota.toExportString());
				}
			}
		}
		catch (IOException e) {
			Log.e("Export data", e.getMessage());
			result = false;
		}

		finally {
			if (writer != null) writer.close();
		}

		return result;
	}

	/* Checks if external storage is available for read and write */
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}

	/* Checks if external storage is available to at least read */
	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state) ||
			Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
	}

}
