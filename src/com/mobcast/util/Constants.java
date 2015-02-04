package com.mobcast.util;


public class Constants {

	private static final String SERVER = "http://www.mobcast.in/";
	private static final String COMPANY = SERVER + "sanofi/";
	
	//for push notifications 
	public static final String PROJECT_ID = "792585773362"; //
	public static final String API_KEY = "AIzaSyBjgqwIDlQMSv2cYO5tqIq7iXUb0pACnrY";
	public static final String user_id = "emailAddress";
	
	//for analytics
	public static final String FLURRY_ID = "RC65RKTGR94RTVXH77ND";
	
	//for app folder
	public static final String APP_FOLDER        ="/.mobcast/";
	public static final String APP_FOLDER_IMG    ="/.mobcast/mobcast_images/";
	public static final String APP_FOLDER_VIDEO  ="/.mobcast/mobcast_videos/";
	public static final String APP_FOLDER_AUDIO  ="/.mobcast/mobcast_audio/";
	public static final String APP_FOLDER_TEMP   ="/.mobcast/temp/";
	
	//for app code	

	public static final String GET_BLOCKS = COMPANY + "getBlocks.php";
	public static final String GET_BRAND_IMAGE = COMPANY + "getBrandImage.php";
	public static final String CHECK_LOGIN = COMPANY + "checkLogin.php";
	public static final String M_UPDATE = COMPANY + "mUpdate.php";
	public static final String CONFIRM_LOGIN = COMPANY + "confirmLogin.php";
	public static final String GET_SECTION_VERSION_UPDATES = COMPANY
			+ "getBlockVersion.php";
	public static final String UPDATE = COMPANY + "mUpdate.php";
	public static final String REFRESH_FEEDS = COMPANY + "refreshFeeds.php";
	public static final String UPDATE_ANNOUNCEMENT_REPORTS = COMPANY
			+ "updateAnnouncementReports.php";
	public static final String UPDATE_AWARDS_REPORTS = COMPANY
			+ "updateAwardsReports.php";
	public static final String BASE_URL = COMPANY;
	public static final String UPDATE_READ = COMPANY + "updateRead.php";
	public static final String CHECK_FEEDBACK = COMPANY + "checkFeedback.php";
	public static final String CHECK_VERSION_UPDATES = COMPANY
			+ "checkVersionUpdates.php";
	public static final String CHECK_RECRUITMENT = COMPANY
			+ "checkRecruitment.php";
	public static final String UPDATE_POST = COMPANY + "updatePost.php";
	public static final String UPDATE_CAPTURE = COMPANY + "updateCapture.php";
	public static final String UPDATE_EMAIL_CLICKED = COMPANY
			+ "updateEmailClicked.php";
	public static final String UPDATE_REFERENCES_SHARED = COMPANY
			+ "updateReferencesShared.php";
	public static final String UPDATE_CALL_CLICKED = COMPANY
			+ "updateCallClicked.php";
	public static final String UPDATE_SHARE = COMPANY + "updateShare.php";
	public static final String UPDATE_TRAINING_REPORTS = COMPANY
			+ "updateTrainingReports.php";
	public static final String UPDATE_PRE_DASHBOARD = COMPANY
			+ "updatePreDashboard.php";
	public static final String UPDATE_FEEDBACK = COMPANY
			+ "updateFeedback.php";
	
	public static final String RESET_PASSWORD = COMPANY
			+ "resetPassword.php";
	
	public static final String INCEN_PRODUCT = COMPANY
			+ "getIncenProductName.php";
	
	public static final String INCEN_BASE = COMPANY
			+ "getIncenBase.php";
	
	public static final String INCEN_SCHEME_PDF = COMPANY
			+ "getIncenSchemePdf.php";
	
	//update link
	public static final String APP_UPDATE_LINK = "http://mobc.in/d/4";
	
	//for reports COMPANY + 
	public static final String KEY_ANNOUNCE_ADDRESS =  COMPANY +"updateAnnouncementReports.php";
	public static final String KEY_NEWS_ADDRESS =  COMPANY +"updateNewsReports.php";
	public static final String KEY_EVENT_ADDRESS =  COMPANY +"updateEventReports.php";
	public static final String KEY_TRAINING_ADDRESS =  COMPANY +"updateTrainingReports.php";
	public static final String KEY_RECRUITMENT_ADDRESS =  COMPANY +"updateRecruitmentReports.php";
	public static final String KEY_AWARDS_ADDRESS =  COMPANY +"updateAwardsReports.php";
	public static final String KEY_FEEDBACK_ADDRESS =  COMPANY +"updateFeedbackReports.php";
	
	//for training rating
	public static final String TRAIN_RATE = COMPANY + "sendTrainingResponse.php";
	
	//for ruppee symbol
	public static final String RUPEE = "fonts/Rupee.ttf";
	
	//NOT REALLY USING THESE VALUES
	public static final int[] mMapMonth = new int[] { 550, 660, 770, 880, 990,
			1100, 1265, 1430, 1815, 2475, 5500, 5775, 6050, 6325, 6600, 6875,
			7150, 7425, 7700, 7975, 8250 };
}