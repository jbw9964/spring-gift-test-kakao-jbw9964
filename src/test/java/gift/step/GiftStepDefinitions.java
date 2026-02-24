package gift.step;

import static io.restassured.RestAssured.given;

import gift.TestDataManipulator;
import gift.application.request.GiveGiftRequest;
import gift.model.Member;
import gift.model.Option;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;

/**
 * 선물하기 관련 Step Definition.
 * <p>
 * 회원·옵션 생성 Given step과 선물 보내기 When step을 포함한다. 선물 API는 {@code Member-Id} 헤더로 발신자를 식별하므로, 헤더 유무에 따른
 * 에러 시나리오도 여기서 처리한다.
 */
public class GiftStepDefinitions {

    private final AcceptanceContext context;
    private final TestDataManipulator dataManipulator;

    public GiftStepDefinitions(AcceptanceContext context, TestDataManipulator dataManipulator) {
        this.context = context;
        this.dataManipulator = dataManipulator;
    }

    // --- Given ---

    @Given("회원 {string}가 이미 존재한다")
    public void memberExists(String name) {
        Member member = dataManipulator.addMember(name, name + "@test.com");

        context.putMemberId(name, member.getId());
    }

    @Given("상품의 옵션 {string}이 재고 {int}으로 이미 존재한다")
    public void optionExists(String name, int quantity) {
        Long productId = context.getLastProductId();
        Option option = dataManipulator.addOption(name, quantity, productId);

        context.putOptionId(name, option.getId());
    }

    // --- When: 정상 선물 ---

    @When("{string}가 {string}에게 옵션 {string} {int}개를 선물한다")
    public void sendGift(String sender, String receiver, String optionName, int quantity) {
        Long senderId = context.getMemberId(sender);
        Long receiverId = context.getMemberId(receiver);
        Long optionId = context.getOptionId(optionName);

        GiveGiftRequest request = new GiveGiftRequest(
                optionId, quantity, receiverId,
                "테스트 선물"
        );

        context.setResponse(
                given()
                        .contentType(ContentType.JSON)
                        .header("Member-Id", senderId)
                        .body(request)
                        .when()
                        .post("/api/gifts")
        );
    }

    // --- When: 옵션 관련 에러 ---

    @When("옵션 id 없이 선물 보내기를 요청한다")
    public void sendGiftWithoutOptionId() {
        Long senderId = context.getMemberId("Gift Sender");
        Long receiverId = context.getMemberId("Gift Receiver");

        GiveGiftRequest request = new GiveGiftRequest(
                null, 1, receiverId,
                "테스트 선물"
        );

        context.setResponse(
                given()
                        .contentType(ContentType.JSON)
                        .header("Member-Id", senderId)
                        .body(request)
                        .when()
                        .post("/api/gifts")
        );
    }

    @When("존재하지 않는 옵션 id로 선물 보내기를 요청한다")
    public void sendGiftWithNonExistentOptionId() {
        Long senderId = context.getMemberId("Gift Sender");
        Long receiverId = context.getMemberId("Gift Receiver");
        Long notExistingId = context.getNotExistingId();

        GiveGiftRequest request = new GiveGiftRequest(
                notExistingId, 1, receiverId,
                "테스트 선물"
        );

        context.setResponse(
                given()
                        .contentType(ContentType.JSON)
                        .header("Member-Id", senderId)
                        .body(request)
                        .when()
                        .post("/api/gifts")
        );
    }

    // --- When: 수신자 관련 에러 ---

    @When("수신자 id 없이 선물 보내기를 요청한다")
    public void sendGiftWithoutReceiverId() {
        Long senderId = context.getMemberId("Gift Sender");
        Long optionId = context.getOptionId("Option 0");

        GiveGiftRequest request = new GiveGiftRequest(
                optionId, 1, null,
                "테스트 선물"
        );

        context.setResponse(
                given()
                        .contentType(ContentType.JSON)
                        .header("Member-Id", senderId)
                        .body(request)
                        .when()
                        .post("/api/gifts")
        );
    }

    @When("존재하지 않는 수신자 id로 선물 보내기를 요청한다")
    public void sendGiftWithNonExistentReceiverId() {
        Long senderId = context.getMemberId("Gift Sender");
        Long optionId = context.getOptionId("Option 0");
        Long notExistingId = context.getNotExistingId();

        GiveGiftRequest request = new GiveGiftRequest(
                optionId, 1, notExistingId,
                "테스트 선물"
        );

        context.setResponse(
                given()
                        .contentType(ContentType.JSON)
                        .header("Member-Id", senderId)
                        .body(request)
                        .when()
                        .post("/api/gifts")
        );
    }

    // --- When: Member-Id 헤더 관련 에러 ---

    @When("Member-Id 헤더 없이 선물 보내기를 요청한다")
    public void sendGiftWithoutMemberIdHeader() {
        Long optionId = context.getOptionId("Option 0");
        Long receiverId = context.getMemberId("Gift Receiver");

        GiveGiftRequest request = new GiveGiftRequest(
                optionId, 1, receiverId, "테스트"
        );

        context.setResponse(
                given()
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .post("/api/gifts")
        );
    }

    @When("존재하지 않는 Member-Id로 선물 보내기를 요청한다")
    public void sendGiftWithNonExistentMemberId() {
        Long optionId = context.getOptionId("Option 0");
        Long receiverId = context.getMemberId("Gift Receiver");
        Long notExistingId = context.getNotExistingId();

        GiveGiftRequest request = new GiveGiftRequest(
                optionId, 1, receiverId,
                "테스트 선물"
        );

        context.setResponse(
                given()
                        .contentType(ContentType.JSON)
                        .header("Member-Id", notExistingId)
                        .body(request)
                        .when()
                        .post("/api/gifts")
        );
    }
}
