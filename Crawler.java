import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.commons.validator.routines.UrlValidator;

import java.io.IOException;

public class Crawler{

  private Crawler(){}; // No need to instantiate objects here, so constructor stays private.

  public static HashSet<String> crawl(String seed_Url, int pages_To_Crawl) throws MalformedURLException{

    // We will return a list of Strings corresponding to "hits" to investigate.

    ArrayDeque<URL> queueOf_SearchableURLs = new ArrayDeque<>(); // Initialize queue of URLs to search
    HashSet<String> visitedSites = new HashSet<>(); // Set to track our progress
    int limit = pages_To_Crawl;

    String[] schemes = {"http", "https"}; // We will be looking at only http and https protocols as our seed

    UrlValidator urlValidatorObj = new UrlValidator(schemes);

    /*
     * Validator made to be predisposed to protocols: HTTP and HTTPS
     */

    if(!urlValidatorObj.isValid(seed_Url)){ // If the URL is invalid, we will stop before we begin.
      System.out.println("Seed URL is invalid");
      return null;
    }

    // If we are at this point, we know the seed_Url is valid.
    try{
    	System.out.println("Seed URL is valid.");
    	queueOf_SearchableURLs.add(new URL(seed_Url)); // Add the validated seed URL to the queue.
    }catch (MalformedURLException ex){

    	ex.printStackTrace();
    }

    while(!queueOf_SearchableURLs.isEmpty() && visitedSites.size() < limit){

      // while the pending URL queue is NOT empty AND we haven't reached the limit of pages to parse.

      URL currentURL = queueOf_SearchableURLs.remove();


      // currentURL is the URL previously at head of the queue.



      // At this point, we KNOW we have a valid, unvisited URL in "currentURL"

      Document myDoc = null; // Solely declared for scoping purposes here.

      try{
    	  /*
    	   * Jsoup.parse(URL myURL, int millisToTimeOut) is a method in JSoup library that allows
    	   * us to parse a URL into a document for operationalization.
    	   */
    	  myDoc = Jsoup.connect(currentURL.toString()).get();
          visitedSites.add(currentURL.toString());

      }catch (IOException ex){
    	  System.out.println("IO");
    	  /*
    	   * Promptly note the IO exception in console and continue
    	   * Note: We do NOT want to stop the program just because a certain URL can't be resolved.
    	   */
    	  continue;
      }

      // We now have an acceptable HTML document to parse via Jsoup connection

      Elements currentURL_Links = myDoc.select("a[href]");

      /* Returns an ArrayList<Element> object, in this case, links to be used.
       * Empty if none are found.
       */


      for(int i = 0; i < currentURL_Links.size(); i++){
    	  /*
    	   * Iterate over each Element object and add them to the queue if they
    	   * fit our specifications.
    	   */
    	  Element currentEle = currentURL_Links.get(i);

    	  /*
    	   * Retrieves the ith element (link) within our list of elements (links)
    	   * Now we have a sole element (link) to work with. (href = "https://www.example.com")
    	   */

    	  String link_Href = currentEle.attr("abs:href");

    	  /*
    	   * Node.attr(String key) method returns the value of the attribute matching our key
    	   * In this case, it's href because we want a String representation of the actual link value.
    	   */



    	  /*
    	   * To circumvent the issue of MalformedURLExceptions, utilize a boolean variable
    	   * and if a MalformedURLException occurs, simply continue, causing the algorithm to pop the
    	   * next URL and proceed.
    	   */

    	  boolean queueContainsLink = true;


    	  try{
    		  queueContainsLink = queueOf_SearchableURLs.contains(new URL(link_Href));
    	  }catch(MalformedURLException ex){
    		  continue;
    	  }


    	  if(!queueContainsLink && !visitedSites.contains(link_Href) && (queueOf_SearchableURLs.size() + visitedSites.size()) < limit){
    		  /*
    		   * If our Pending URL queue AND VisitedSite Set do NOT contain the link
    		   * AND we haven't reached our limit of links.
    		   */

    		  if(link_Href.contains("#") || !(urlValidatorObj.isValid(link_Href))){
    			  /*
    			   * If the link contains a pound sign OR it is deemed invalid by our
    			   * Http and Https URL validator, re-loop WITHOUT adding anything to the
    			   * search queue.
    			   *
    			   */
    			  continue;
    		  }

    		  queueOf_SearchableURLs.add(new URL(link_Href));

    	  } else continue;


      }

      /*
       *  Don't forget to add the current URL to the HashSet for future reference.
       *  Note that adding duplicates to the HashSet won't actually put them in, so we don't
       *  need an if-statement before we add the current URL.
       */


      System.out.println("Just visited: " + currentURL.toString() +  "\nSize of Path: " + visitedSites.size());
      // Test code.

    }

    return visitedSites; // return our URL path.

  }

  public static void main(String[] args) throws MalformedURLException{


	  HashSet<String> pathTraveled = crawl("https://news.ycombinator.com/", 15);


	  Iterator<String> iterator = pathTraveled.iterator();
	  /*
	   * Iterator will serve to print out each URL in string format to show us where
	   * we've been, No duplicates.
	   */

	  while(iterator.hasNext()){
		  System.out.println(iterator.next());
	  }


  } // main

} // class