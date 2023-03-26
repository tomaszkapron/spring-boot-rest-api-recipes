package recipes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import recipes.model.Recipe;
import recipes.service.RecipeService;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class RecipeController {

    @Autowired
    RecipeService recipeService;

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/api/recipe/all")
    public List<Recipe> getAllRecipes() {
        //TODO: add pagination
        return recipeService.getAllRecipes();
    }

    @GetMapping("/api/recipe/{id}")
    public Recipe getRecipe(@PathVariable Long id) {
        Optional<Recipe> optionalRecipe = recipeService.getRecipeById(id);
        if (optionalRecipe.isPresent()) {
            return optionalRecipe.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/api/recipe/search")
    @ResponseBody
    public List<Recipe> getRecipeByCatOrName(@RequestParam(required = false) String category,
                                             @RequestParam(required = false) String name) {
        if (name != null & category != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (name == null & category == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (name != null) {
            return recipeService.findByNameIgnoreCaseContainsOrderByDateDesc(name);
        }
        return recipeService.findByCategoryIgnoreCaseOrderByDateDesc(category);
    }

    @PostMapping("/api/recipe/new")
    public String postRecipe(@AuthenticationPrincipal UserDetails details, @Valid @RequestBody Recipe recipe) {
        recipe.setOwnerUser(details.getUsername());
        Recipe saveRecipe = recipeService.saveRecipe(recipe);
        return String.format("{\"id\": %d}", saveRecipe.getId());
    }

    @PutMapping("/api/recipe/{id}")
    public ResponseEntity<String> updateRecipe(@AuthenticationPrincipal UserDetails details,
                                               @PathVariable Long id,
                                               @Valid @RequestBody Recipe recipe) {
        if (!recipeService.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Recipe recipeToBeUpdated = recipeService.getRecipeById(id).get();
        if (!Objects.equals(details.getUsername(), recipeToBeUpdated.getOwnerUser())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        recipe.setId(id);
        Recipe saveRecipe = recipeService.saveRecipe(recipe);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/api/recipe/{id}")
    public ResponseEntity<String> deleteRecipe(@AuthenticationPrincipal UserDetails details,
                                               @PathVariable Long id) {
        String recipeOwner;
        Optional<Recipe> optionalRecipe = recipeService.getRecipeById(id);
        if (optionalRecipe.isPresent()) {
            recipeOwner =  optionalRecipe.get().getOwnerUser();
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        if (!Objects.equals(details.getUsername(), recipeOwner) && recipeOwner != null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            recipeService.deleteById(id);
            return new ResponseEntity<>(
                    "Deleted",
                    HttpStatus.NO_CONTENT);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(
                    "No recipe with given id!",
                    HttpStatus.NOT_FOUND);
        }
    }
}
