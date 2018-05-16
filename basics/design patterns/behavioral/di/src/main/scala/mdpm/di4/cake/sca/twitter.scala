package mdpm.di4.cake
package sca

import java.util.Date
import java.text.DateFormat

// cf. http://ofps.oreilly.com/titles/9780596155957/ApplicationDesign.html

// Datatypes ------------------------------------------------------------------

class TwitterUserProfile(val userName: String) {
  override def toString: String = "@" + userName
}

case class Tweet(val tweeter: TwitterUserProfile, val message: String, val time: Date) {
  override def toString: String =
    "(" + DateFormat.getDateInstance(DateFormat.FULL).format(time) + ") " + tweeter + ": " + message
}

// Specs ----------------------------------------------------------------------

trait Tweeter {
  def tweet(message: String)
}

// Components -----------------------------------------------------------------

// Simple Components ------------------

@Component trait TwitterClientUIComponent {
  @ProvidedService val ui: TwitterClientUI

  abstract class TwitterClientUI(val client: Tweeter) {
    def sendTweet(message: String): Unit = client.tweet(message)

    def showTweet(tweet: Tweet): Unit
  }

}

@Component trait TwitterLocalCacheComponent {
  @ProvidedService val localCache: TwitterLocalCache

  trait TwitterLocalCache {
    def saveTweet(tweet: Tweet): Unit

    def history: List[Tweet]
  }

}

@Component trait TwitterServiceComponent {
  @ProvidedService val service: TwitterService

  trait TwitterService {
    def sendTweet(tweet: Tweet): Boolean

    def history: List[Tweet]
  }

}

// Composed Components ----------------

@ComposedComponent trait TwitterClientComponent {
  self: TwitterClientUIComponent
    with TwitterLocalCacheComponent
    with TwitterServiceComponent =>

  @ProvidedService val client: TwitterClient

  class TwitterClient(val user: TwitterUserProfile) extends Tweeter {
    def tweet(msg: String): Unit = {
      val twt = new Tweet(user, msg, new Date)
      if (service.sendTweet(twt)) {
        localCache.saveTweet(twt)
        ui.showTweet(twt)
      }
    }
  }

}

// Concrete Client ------------------------------------------------------------

class TextClient(userProfile: TwitterUserProfile)
  extends TwitterClientComponent
  with TwitterClientUIComponent
  with TwitterLocalCacheComponent
  with TwitterServiceComponent {

  // === From TwitterClientComponent:
  val client = new TwitterClient(userProfile)

  // === From TwitterClientUIComponent:
  val ui = new TwitterClientUI(client) {
    def showTweet(tweet: Tweet) = println(tweet)
  }

  // === From TwitterLocalCacheComponent:
  val localCache = new TwitterLocalCache {
    private var tweets: List[Tweet] = Nil

    def saveTweet(tweet: Tweet) = tweets ::= tweet

    def history = tweets
  }

  // === From TwitterServiceComponent
  val service = new TwitterService() {
    def sendTweet(tweet: Tweet) = {
      println("Sending tweet to Twitter HQ")
      true
    }

    def history = List[Tweet]()
  }
}

object Main extends App {

  val client = new TextClient(new TwitterUserProfile("normenmueller"))
  client.ui.sendTweet("My First Tweet. How's this thing work?")
  client.ui.sendTweet("Is this thing on?")
  client.ui.sendTweet("Heading to the bathroom...")

  println("Chat history:")
  client.localCache.history.foreach {
    t => println(t)
  }

}
