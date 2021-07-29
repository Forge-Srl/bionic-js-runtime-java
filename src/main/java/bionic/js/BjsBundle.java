package bionic.js;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemNotFoundException;
import java.util.ArrayList;
import java.util.List;

class BjsBundle
{
    private final Class<?> clazz;
    final String name;

    BjsBundle(@NonNull Class<?> forClass, @NonNull String name)
    {
        this.name = name;
        this.clazz = forClass;
    }

    ClassLoader getLoader()
    {
        return clazz.getClassLoader();
    }

    String getFullPathName(@NonNull String requirePath)
    {
        String bundleDir = name + ".bundle";
        return requirePath.equals("/")
                ? bundleDir
                : requirePath.startsWith("/")
                ? String.format("%s%s", bundleDir, requirePath)
                : String.format("%s/%s", bundleDir, requirePath);
    }

    @CheckForNull
    String loadFile(@NonNull String requirePath)
    {
        String filePath = getFullPathName(requirePath);

        List<String> lines = null;
        try
        {
            // 1. Try to load file assuming it's outside of the jar.
            URL resource = getLoader().getResource(filePath);
            if (resource == null)
            {
                throw new FileNotFoundException(filePath);
            }

            URI uri = resource.toURI();
            if (!uri.getScheme().equals("file"))
            {
                throw new FileNotFoundException(filePath);
            }

            File file = new File(uri);
            try (FileInputStream stream = new FileInputStream(file))
            {
                lines = streamToStrings(stream);
            }
        }
        catch (IOException | URISyntaxException | FileSystemNotFoundException e1)
        {
            // 2. Try to load file assuming it's inside of the jar (i.e. as stream).
            try (InputStream fileStream = getLoader().getResourceAsStream(filePath))
            {
                if (fileStream == null)
                {
                    throw new FileNotFoundException(filePath);
                }

                lines = streamToStrings(fileStream);
            }
            catch (IOException e2)
            {
                // Ignore.
            }
        }

        return lines == null ? null : String.join("\n", lines);
    }

    private static List<String> streamToStrings(InputStream stream) throws IOException
    {
        try (InputStreamReader in = new InputStreamReader(stream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(in))
        {
            String line;
            List<String> lines = new ArrayList<>();
            while ((line = reader.readLine()) != null)
            {
                lines.add(line);
            }
            return lines;
        }
    }
}
