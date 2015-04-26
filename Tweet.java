package CommentSeeker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class Tweet {

	static ResponseList<Status> statuses;  

	static Twitter twitter;
	static ConfigurationBuilder cb = new ConfigurationBuilder();
	TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
	//static String line = "";
	public static void main(String[] args) throws TwitterException {

		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("IKIdiJ1jMSzA9uOXZ9vMHHCGo")
		.setOAuthConsumerSecret("bb5qPG9wYud0lBiWEkNTcFUfFq2opBlhpsE0riMZVBpLGJSkn1")
		.setOAuthAccessToken("414012971-0d8y97wAU4lRlDYide7ku2wP4bXlxWvan7GHnuc6")
		.setOAuthAccessTokenSecret("V8UoMUhJmvwNAkKwutUSfU4MiJCMb5G3diGA6PbGBMr4U");

		TwitterFactory factory = new TwitterFactory(cb.build());
		twitter = factory.getInstance();

		IDs followersIDs = twitter.getFollowersIDs(-1);
		IDs friendsIDs = twitter.getFriendsIDs(-1);
		long[]followerArray = followersIDs.getIDs();
		long[]friendArray = friendsIDs.getIDs();	

		// print ids in the array
		/**
		for (int i = 0; i<followerArray.length; i++) {
			System.out.println("FollowerIds : "+ followerArray[i]);		
		}
		for (int i = 0; i<friendArray.length; i++) {
			System.out.println("FollowingsIds : "+ friendArray[i]);
		}*/

		try {
			FileInputStream inputFile = new FileInputStream("dressType.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(inputFile,"UTF-8"));
			String line = "";
			while ((line = br.readLine()) != null) {
				if (line!= null) {
					//System.out.println (line);
					lookupUsers(followerArray,line);
				}
			}
			br.close();
			inputFile.close();			
		} catch (Exception e){
			e.printStackTrace();
		} 
	}

	// to print the ScreenName 
	/**
		do
			{
				for (long i : friendsIDs.getIDs())
				{
					System.out.println(twitter.showUser(i).getName());
					System.out.println("following ID #" + i);
				}
			} while(friendsIDs.hasNext()); */

	// works good
	public static void lookupUsers(long[] usersList, String keyword) {
		try {
			//Twitter twitter = new TwitterFactory().getInstance();
			ResponseList<User> users = twitter.lookupUsers(usersList);
			Paging paging = new Paging(1, 100);
			List<Status> statuses;
			ArrayList<String> twt = new ArrayList<String>();

			for (User user : users) {
				statuses = twitter.getUserTimeline(user.getScreenName(), paging);
				System.out.println("\nUser: @" + user.getScreenName());
				for (Status s : statuses) {			
					//System.out.println(s.getText())
					
						twt.add(s.getText()); 
						//Object o = twt.get(i);
						//if(o instanceof String) {
							//System.out.println("Value is "+ o.toString());    
							searchForWord(keyword, twt);
				}
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	public static ResponseList<Status> getFriendsTimeline(long[] i) throws TwitterException {
		long[] srch = i;
		ResponseList<User> users1 = twitter.lookupUsers(srch);

		for (User user : users1) {
			System.out.println("Friend's Name " + user.getName()); 
			if (user.getStatus() != null) 
			{
				//System.out.println("Friend's timeline:");
				List<Status> statusess = twitter.getUserTimeline(user.getName());
				for (Status stats : statusess) 
				{
					//SearchResults results = twitter.searchOperations().search("#spring");
					//System.out.println(stats.getText());
					stats.getText();
				}
			}
		}
		return statuses;
	}

	public static void searchForWord(String word, ArrayList tweets) throws TwitterException {

		Query query = new Query(word);
		query.setCount(100);
		QueryResult result = twitter.search(query);
		tweets = (ArrayList) result.getTweets();
		for (int i = 0; i < tweets.size(); i++) {
			Status t = (Status) tweets.get(i);
			String lang = t.getLang();
			//user = t.getUser().getScreenName();		
			//System.out.println("Tweeted by " + t.getContributors() + " at "  + ": " + t.getText());
			if (lang.equals("en")) {
				System.out.println(t.getText());
				if(cleanTweet(t)==-1) {
					tweets.remove(i);
				}
			}
			//storeInFile();
		}
	}

	public static int cleanTweet(Status twt) throws TwitterException {

		if (twt.isRetweet()) {			
			//twitter.destroyStatus(twt);
			return -1;
		} 
		return 0;
	}

	public static void saveToFile(String Source,String filename) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("Resource" + File.separator + filename+".txt"));

			for(String line : Source.split("\\r?\\n")){
				bw.write(line);
				bw.newLine();
			}
			bw.flush(); 
			System.out.println("\nSource FIle written\n");
			bw.close();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}


//List<Status> stats = twitter.getUserTimeline(new Paging(1, 200));
/**
		for (Status status : statuses)
		{
			if (status.isRetweet())
			{
				// Check original Tweeter
				Status theOriginalTweet = status.getRetweetedStatus();
				//if (theOriginalTweet.getURLEntities() != null )


				if (theOriginalTweet.getUser().getScreenName().equals("Asem"))
				{
					// Get current date, then subtract 24hrs
					Calendar current = new GregorianCalendar();
					current.setTime(new Date());
					current.add(Calendar.DAY_OF_MONTH, -1);

					// Get tweet time in Calendar format
					Calendar tweetTime = new GregorianCalendar();
					tweetTime.setTime(status.getCreatedAt());

					// Delete the tweet if applicable
					if (tweetTime.before(current))
					{
						System.out.println("Attempting deletion");

						twitter.destroyStatus(status.getId());

						System.out.println("Tweet deleted successfully"); // Doesn't print
					}
				}
			}
		}
 */
