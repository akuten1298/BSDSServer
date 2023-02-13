/**
 * @author aakash
 */
public class SwipePostResponse {

  private boolean success;
  private String description;
  private int status;
  public SwipePostResponse() {
  }

  public SwipePostResponse(boolean success, String description, int status) {
    this.success = success;
    this.description = description;
    this.status = status;
  }

  public boolean isSuccess() {
    return success;
  }

  public String getDescription() {
    return description;
  }

  public int getStatus() {
    return status;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return "SwipePostResponse{" +
        "success=" + success +
        ", description='" + description + '\'' +
        ", status=" + status +
        '}';
  }
}
