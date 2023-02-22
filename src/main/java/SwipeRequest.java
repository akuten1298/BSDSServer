/**
 * @author aakash
 */
public class SwipeRequest {
  private String swiper;

  private String swipee;

  private String comment;

  private String swipeDirection;

  public String getSwipeDirection() {
    return swipeDirection;
  }

  public void setSwipeDirection(String swipeDirection) {
    this.swipeDirection = swipeDirection;
  }

  public SwipeRequest(String swiper, String swipee, String comment) {
    this.swiper = swiper;
    this.swipee = swipee;
    this.comment = comment;
  }

  public String getSwiper() {
    return swiper;
  }

  public String getSwipee() {
    return swipee;
  }

  public String getComment() {
    return comment;
  }

  public String convertToQueueMessage() {
    return "swiper=" + swiper +
        ", swipee=" + swipee +
        ", comment=" + comment +
        ", swipeDirection=" + swipeDirection;
  }

  @Override
  public String toString() {
    return "SwipeRequest{" +
        "swiper='" + swiper + '\'' +
        ", swipee='" + swipee + '\'' +
        ", comment='" + comment + '\'' +
        ", swipeDirection='" + swipeDirection + '\'' +
        '}';
  }
}
