/**
 * 
 */
package co.phystech.aosorio.app;

import static spark.Spark.post;
import static spark.Spark.get;
import static spark.Spark.put;
//  import static spark.Spark.before;

import static spark.Spark.options;
import static spark.Spark.port;

//  import co.phystech.aosorio.services.AuthorizeSvc;
import co.phystech.aosorio.services.GeneralSvc;
import co.phystech.aosorio.services.StatisticsSvc;
import co.phystech.aosorio.config.CorsFilter;
import co.phystech.aosorio.config.Routes;
import co.phystech.aosorio.controllers.MaterialsController;
import co.phystech.aosorio.controllers.ProvidersController;
import co.phystech.aosorio.controllers.QuotesController;
import co.phystech.aosorio.controllers.RequestForQuotesController;

/**
 * @author AOSORIO
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		port(getHerokuAssignedPort());

		CorsFilter.apply();

		get("/hello", (req, res) -> "Fiche DB service deployed");

		// .. Authorization
		//if (args.length == 0)
		//	before(Routes.AUTH + "*", AuthorizeSvc::authorizeUser);

		// ... Materials
		post(Routes.MATERIALS, MaterialsController::create, GeneralSvc.json());
		
		post(Routes.MATERIALS + "/xcheck", MaterialsController::xcheck, GeneralSvc.json());
		
		post(Routes.MATERIALS + "/xcheck/:id", MaterialsController::singlexcheck, GeneralSvc.json());
		
		post(Routes.MATERIALS + "/:id", MaterialsController::read, GeneralSvc.json());
		
		put(Routes.MATERIALS + "/:id", MaterialsController::update, GeneralSvc.json());
		
		post(Routes.MATERIALS + "/weights/:id", MaterialsController::calculateWeight, GeneralSvc.json());
		
		// ... RFQs
		
		post(Routes.RFQS, RequestForQuotesController::create, GeneralSvc.json());
		
		get(Routes.RFQS + "/:id", RequestForQuotesController::read, GeneralSvc.json());
		
		post(Routes.RFQS + "/quotes", RequestForQuotesController::quoteFinder, GeneralSvc.json());
		
		// ... Quotes
		
		post(Routes.QUOTES, QuotesController::create, GeneralSvc.json());
		
		get(Routes.QUOTES + "/:id", QuotesController::read, GeneralSvc.json());
		
		// ... Providers
		
		post(Routes.PROVIDERS, ProvidersController::create, GeneralSvc.json());
					
		get(Routes.PROVIDERS, ProvidersController::readAll, GeneralSvc.json());
		
		post(Routes.PROVIDERS + "/:id", ProvidersController::read, GeneralSvc.json());
		
		put(Routes.PROVIDERS + "/:id", ProvidersController::update, GeneralSvc.json());
		
		post(Routes.PROVIDERS + "/comments/:id", ProvidersController::addComment, GeneralSvc.json());
		
		// ... Statistics

		get("/statistics", StatisticsSvc::getBasicStats);

		options("/*", (request, response) -> {

			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
			if (accessControlRequestHeaders != null) {
				response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
			}
			String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
			if (accessControlRequestMethod != null) {
				response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
			}
			return "OK";
		});

	}

	static int getHerokuAssignedPort() {
		ProcessBuilder processBuilder = new ProcessBuilder();
		if (processBuilder.environment().get("PORT") != null) {
			return Integer.parseInt(processBuilder.environment().get("PORT"));
		}
		return 4567;
	}

}
