package project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class DownloadFilesFromOneDriveUrl {
	
	private static final int BUFFER_SIZE = 4096;

	private void downloadFile(String fileURL, String saveDir) throws IOException {
		if (fileURL.startsWith("https://1drv.ms/")) {
			fileURL = urlFormatter(fileURL);
		}
		if(fileURL!="") {
		URL url = new URL(fileURL);

		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		int responseCode = httpConn.getResponseCode();
		// always check HTTP response code first
		if (responseCode == HttpURLConnection.HTTP_OK) {
			String fileName = "";
			String disposition = httpConn.getHeaderField("Content-Disposition");
			
			if (disposition != null) {
				// extracts file name from header field
				int index = disposition.indexOf("filename=");
				if (index > 0) {
					fileName = disposition.substring(index + 10, disposition.length() - 1);
				}
			} else {
				// extracts file name from URL
				fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
			}

			System.out.println("fileName = " + fileName);
			// opens input stream from the HTTP connection
			InputStream inputStream = httpConn.getInputStream();
			String saveFilePath = saveDir + File.separator + fileName;

			// opens an output stream to save into file
			FileOutputStream outputStream = new FileOutputStream(saveFilePath);

			int bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			outputStream.close();
			inputStream.close();

			System.out.println("File download Successful");
		} else {
			System.out.println("No file to download. Server replied HTTP code: " + responseCode);
		}
		httpConn.disconnect();
		} else {
			System.out.println("Url is not valid");
		}
	}

	//modify url
	private String urlFormatter(String fileURL) throws IOException {
		if(fileURL.contains("onedrive.live.com/redir")) {
			return fileURL.replace("redir", "download");
		}
		else {
		URL url = new URL(fileURL);

		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		int responseCode = httpConn.getResponseCode();
		// always check HTTP response code first
		if (responseCode == HttpURLConnection.HTTP_OK) {
			fileURL = httpConn.getURL().toString().replace("redir", "download");
			
		} else {
			fileURL = "";
			System.out.println("Cannot get refactored Url from Onedrive. Server replied HTTP code: " + responseCode);
			
		}
		httpConn.disconnect();
		return fileURL;
		}
	}

	public void downloadFilesFromOneDriveUrl(String url) throws IOException {
		String dir = "C:\\temporary\\";
		downloadFile(url, dir);
		
		
	}

}
