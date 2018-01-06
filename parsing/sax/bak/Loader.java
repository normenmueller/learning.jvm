import static fj.data.Validation.fail;
import static fj.data.Validation.success;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Date;

import fj.F;
import fj.data.Either;
import fj.data.Option;
import fj.data.Validation;

// TODO lift into `IO`
public class Loader {
 
  static final Date date = new Date();
  
  static final DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

  static F<String, Validation<String,String>> prop = x -> Option.fromString(System.getProperty(x)).toValidation(
    new StringBuilder("System property `").append(x).append("` could not be resolved").toString());

  /* Note: Use `StringBuilder` explicitly on the strength of performance.
   * Cf. http://www.pellegrino.link/2015/08/22/string-concatenation-with-java-8.html */
  static F<String,Validation<String,String>> stamp = x -> Validation.validation(Option.fromString(x).map(y ->
    new StringBuilder(y).append(df.format(date)).append("_").append(date.getTime()).toString()
  ).toEither(new StringBuilder("Invalid argument `").append(x).append("` to stamp.").toString()));
  
  static F<String,Validation<String,Path>> toPath = x -> {
    try {
      return success(Paths.get(x));
    } catch (InvalidPathException e) {
      return fail(e.getMessage().trim());     
    }
  };

  static F<Path,Validation<String,Path>> mkdir = x -> {
    try {
      return success(Files.createDirectories(x));
    } catch (IOException e) {
      return fail(e.getMessage());
    } 
  };
  
  static F<Path,Validation<String, Path>> xml2sql = x -> Validation.fail("failed: "+x);

  public static void load(Path in) {
    Validation.validation(Either.<String,String>right("java.io.tmpdir"))
      .bind(prop).bind(stamp).bind(toPath).bind(mkdir);
  }

}
