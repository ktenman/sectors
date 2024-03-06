package e2e;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.By.id;

class SubmitFormE2ETest {
	
	private static final String DEFAULT_NAME = "Konstantin";
	
	@Test
	void createProfile() {
		open("http://localhost:61234");
		
		$(id("name")).shouldNotHave(text(DEFAULT_NAME)).setValue(DEFAULT_NAME);
		ElementsCollection sectors = $(id("sectors")).getOptions();
		sectors.find(text("Construction materials")).click();
		sectors.find(text("Beverages")).click();
		$(id("terms")).click();
		$(id("submitFormButton")).click();
		
		$(id("formAlert")).shouldBe(visible).shouldHave(text("Profile saved successfully"));
		assertThat(sectors).hasSize(79);
		Selenide.refresh();
		$(id("formAlert")).shouldNotBe(visible);
		assertThat($(By.id("name")).val()).isEqualTo(DEFAULT_NAME);
	}
	
}
