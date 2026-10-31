
import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

public class Main {

    // Шаг 1. Собственная аннотация
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface MyAnnotation {
        String value();
    }

    // Аннотация для проверки версий методов
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Version {
        String number();
        String author();
    }

    // Шаг 2. Использование MyAnnotation
    static class Test {
        @MyAnnotation("Пример собственной аннотации")
        public void testMethod() {
            System.out.println("Метод с MyAnnotation");
        }

        public void normalMethod() {
            System.out.println("Метод без MyAnnotation");
        }
    }

    // Шаг 3. Обработка MyAnnotation через рефлексию
    static void checkMyAnnotation() throws Exception {
        System.out.println("=== MyAnnotation ===");

        Method method = Test.class.getDeclaredMethod("testMethod");
        MyAnnotation annotation =
                method.getAnnotation(MyAnnotation.class);

        if (annotation != null) {
            System.out.println("Значение: " + annotation.value());
        }

        Method normal = Test.class.getDeclaredMethod("normalMethod");
        MyAnnotation absent =
                normal.getAnnotation(MyAnnotation.class);

        if (absent == null) {
            System.out.println("У normalMethod аннотации нет.");
        }
    }

    // Шаг 4. Встроенная аннотация Deprecated
    static class Example {
        @Deprecated
        public void oldMethod() {
            System.out.println("Вызван устаревший метод.");
        }
    }

    // Задача: два метода с аннотацией Version
    static class Software {
        @Version(number = "1.0", author = "Alice")
        public void featureOne() {
            System.out.println("Feature One");
        }

        @Version(number = "1.1", author = "Bob")
        public void featureTwo() {
            System.out.println("Feature Two");
        }

        // Дополнительное задание: метод без Version
        public void featureThree() {
            System.out.println("Feature Three");
        }
    }

    // Класс без единой аннотации Version
    static class PlainSoftware {
        public void first() {
        }

        public void second() {
        }
    }

    // Проверка версий всех методов
    static class VersionChecker {
        public static void check(Class<?> type) {
            Method[] methods = type.getDeclaredMethods();

            // Сортируем, чтобы вывод всегда был в одном порядке
            Arrays.sort(methods, Comparator.comparing(Method::getName));

            int withoutVersion = 0;

            for (Method method : methods) {
                Version version = method.getAnnotation(Version.class);

                if (version != null) {
                    System.out.println(
                            "Method: " + method.getName()
                            + ", Version: " + version.number()
                            + ", Author: " + version.author()
                    );
                } else {
                    withoutVersion++;
                }
            }

            System.out.println(
                    "Методов без @Version: " + withoutVersion
            );
        }
    }

    public static void main(String[] args) throws Exception {

        checkMyAnnotation();

        System.out.println("\n=== Deprecated ===");
        Example example = new Example();
        example.oldMethod();

        System.out.println("\n=== Version ===");
        Software software = new Software();
        software.featureOne();
        software.featureTwo();
        software.featureThree();

        VersionChecker.check(Software.class);

        System.out.println("\n=== Класс без Version ===");
        VersionChecker.check(PlainSoftware.class);
    }
}
