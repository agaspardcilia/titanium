package utils;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import screach.titanium.core.wsp.WebApiException;
import utils.webapi.HttpException;

public class ErrorUtils {
	public static boolean isSuccessful(JSONObject answer) {
		return answer.has("success") && answer.getBoolean("success");
	}

	public static WebApiException parseError(JSONObject answer) {
		if (answer.has("errorCode")) {
			return new WebApiException(answer.getInt("errorCode"), answer.getString("errorMessage"));
		} else {
			return new WebApiException(-1, "Unknown error.");
		}
	}

	// TODO : Show stack trace in debug mode.
	public static Alert getAlertFromException(Exception e) {
		Alert result;

		if (e instanceof JSONException) {
			result = newErrorAlert("JSON Error", "Error while parsing server answer.", e.getClass() + " : " + e.getMessage());
		} else if (e instanceof WebApiException) {
			WebApiException wae = (WebApiException) e;
			result = newErrorAlert("Web API error", "The server answered an error.",
					wae.getCode() + ":" + wae.getErrorMessage());
		} else if (e instanceof IOException || e instanceof HttpException) {
			result = newErrorAlert("IO Error", "Error while sending request to server.", e.getClass() + " : " + e.getMessage());
		} else {
			result = newErrorAlert("Error", "Unknown Error. ", e.getClass() + " : " + e.getMessage());
		}

		return result;

	}

	public static Alert newAlert(AlertType type, String title, String mainText, String details) {
		Alert result = new Alert(type);

		result.setTitle(title);
		result.setHeaderText(mainText);
		result.setContentText(details);

		return result;
	}

	public static Alert newErrorAlert(String title, String mainText, String details) {
		return newAlert(AlertType.ERROR, title, mainText, details);
	}
}
