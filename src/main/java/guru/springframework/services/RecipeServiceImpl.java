package guru.springframework.services;

import org.springframework.stereotype.Service;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by jt on 6/13/17.
 */
@Service
public class RecipeServiceImpl implements RecipeService {

	private final RecipeReactiveRepository recipeReactiveRepository;
    private final RecipeCommandToRecipe recipeCommandToRecipe;
    private final RecipeToRecipeCommand recipeToRecipeCommand;

    public RecipeServiceImpl(RecipeReactiveRepository recipeReactiveRepository,
    		RecipeCommandToRecipe recipeCommandToRecipe, 
    		RecipeToRecipeCommand recipeToRecipeCommand) {
    	
    	this.recipeReactiveRepository = recipeReactiveRepository;
        this.recipeCommandToRecipe = recipeCommandToRecipe;
        this.recipeToRecipeCommand = recipeToRecipeCommand;
    }

    @Override
    public Flux<Recipe> getRecipes() {
        return recipeReactiveRepository.findAll();
    }

    
    @Override
    public Mono<Recipe> findById(String id) {
        return recipeReactiveRepository.findById(id);
    }

    @Override
    public Mono<RecipeCommand> findCommandById(String id) {

    	return recipeReactiveRepository.findById(id)
    			.map(recipe -> {
    				RecipeCommand recipeCom = recipeToRecipeCommand.convert(recipe);
    				
					recipeCom.getIngredients().forEach(ingredient -> {
						ingredient.setRecipeId(recipeCom.getId());
					});
    					
    				return recipeCom;
    				});
    }

    @Override
    public Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command) {
    	return recipeReactiveRepository.save(recipeCommandToRecipe.convert(command))
    			.map(savedRecipe -> recipeToRecipeCommand.convert(savedRecipe));
    	//.map(recipeToRecipeCommand::convert);
    }

    @Override
    public void deleteById(String idToDelete) {
    	recipeReactiveRepository.deleteById(idToDelete).block();
    }
}
