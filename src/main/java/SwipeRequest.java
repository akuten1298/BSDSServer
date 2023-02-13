/**
 * @author aakash
 */
public class SwipeRequest {
  private String swiper;

  private String swipee;

  private String comment;

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

  @Override
  public String toString() {
    return "SwipeRequest{" +
        "swiper='" + swiper + '\'' +
        ", swipee='" + swipee + '\'' +
        ", comment='" + comment + '\'' +
        '}';
  }
}
