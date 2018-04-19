import model.Pizza;
import model.PizzaDao;
import spark.ModelAndView;
import spark.Request;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.after;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

public class PizzaController {
    public static void main(String[] args) {
        port(getHerokuAssignedPort());
        exception(Exception.class, (e, req, res) -> e.printStackTrace()); // print all exceptions
        staticFiles.location("/public");

        get("/", (req, res) -> renderPizzas(req));
//        get("/pizza/:id/edit",          (req, res)      -> renderEditPizza(req));

        post("/pizzas", (req, res) -> {
            PizzaDao.add(Pizza.create(req.queryParams("pizza-name")));
            return renderPizzas(req);
        });
//        delete("/todos/completed",      (ICRoute) (req) -> TodoDao.removeCompleted());
//        delete("/todos/:id",            (ICRoute) (req) -> TodoDao.remove(req.params("id")));
//        put("/todos/toggle_status",     (ICRoute) (req) -> TodoDao.toggleAll(req.queryParams("toggle-all") != null));
//        put("/todos/:id",               (ICRoute) (req) -> TodoDao.update(req.params("id"), req.queryParams("todo-title")));
//        put("/todos/:id/toggle_status", (ICRoute) (req) -> TodoDao.toggleStatus(req.params("id")));

        after((req, res) -> {
            if (res.body() == null) { // if the route didn't return anything
                res.body(renderPizzas(req));
            }
        });
    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567;
    }

    private static String renderPizzas(Request req) {
        String statusStr = req.queryParams("status");
        Map<String, Object> model = new HashMap<>();
        model.put("pizzas", PizzaDao.all());
        model.put("count", PizzaDao.count());
        if ("true".equals(req.queryParams("ic-request"))) {
            return renderTemplate("velocity/pizzaList.vm", model);
        }
        return renderTemplate("velocity/index.vm", model);
    }

    private static String renderTemplate(String template, Map model) {
        return new VelocityTemplateEngine().render(new ModelAndView(model, template));
    }

}
