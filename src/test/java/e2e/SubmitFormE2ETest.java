package e2e;

import com.codeborne.selenide.ElementsCollection;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.anyOf;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.By.id;

class SubmitFormE2ETest {
	
	@Test
	void submitFormE2ETest() {
		open("http://localhost:61234");
		
		$(id("name")).setValue("Konstantin");
		ElementsCollection sectors = $(id("sectors")).getOptions();
		sectors.find(text("Construction materials")).click();
		sectors.find(text("Beverages")).click();
		$(id("terms")).click();
		$(id("submitFormButton")).click();
		
		$(id("formAlert")).shouldHave(anyOf("Profile status",
				text("Profile saved successfully"),
				text("Profile updated")));
		assertThat(sectors).hasSize(80);
	}
	
}
