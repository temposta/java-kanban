package ru.temposta.app.mappers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import ru.temposta.app.model.Epic;
import ru.temposta.app.model.Subtask;
import ru.temposta.app.model.Task;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;

public class GsonMapper {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
            .serializeNulls()
            .setPrettyPrinting()
            .create();
    private static final DateTimeFormatter LDT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    //_______________________________________
    //раздел для преобразования в строку JSON
    //_______________________________________

    //Task, Subtask, Epic --> String

    public Function<Task, String> toJSON() {
        return GsonMapper::apply;
    }

    private static String apply(Task task) {
        return gson.toJson(task);
    }


    //List<Task>, List<Subtask>, List<Epic> --> String

    public Function<List<Task>, String> toJSONTaskList() {
        return GsonMapper::applyToListOfTasks;
    }

    public Function<List<Subtask>, String> toJSONSubtaskList() {
        return GsonMapper::applyToListOfSubtasks;
    }

    public Function<List<Epic>, String> toJSONEpicList() {
        return GsonMapper::applyToListOfEpics;
    }


    private static String applyToListOfTasks(List<Task> ts) {
        return gson.toJson(ts);
    }

    private static String applyToListOfSubtasks(List<Subtask> ts) {
        return gson.toJson(ts);
    }

    private static String applyToListOfEpics(List<Epic> ts) {
        return gson.toJson(ts);
    }

    //_______________________________________
    //раздел для преобразования в объекты приложения
    //_______________________________________

    //String --> Task

    public Function<String, Task> toTaskEntity() {
        return GsonMapper::apply;
    }

    private static Task apply(String s) {
        return gson.fromJson(s, Task.class);
    }

    //String --> List<Task>

    public Function<String, List<Task>> toListOfTasksEntity() {
        return GsonMapper::applyToListOfTasks;
    }

    private static List<Task> applyToListOfTasks(String s) {
        return gson.fromJson(s, new TypeToken<List<Task>>() {
        });
    }

    //String --> Subtask

    public Function<String, Subtask> toSubtaskEntity() {
        return GsonMapper::applyToSubtaskEntity;
    }

    private static Subtask applyToSubtaskEntity(String s) {
        return gson.fromJson(s, Subtask.class);
    }

    //String --> List<Subtask>

    public Function<String, List<Subtask>> toListOfSubtasksEntity() {
        return GsonMapper::applyToListOfSubtasks;
    }

    private static List<Subtask> applyToListOfSubtasks(String s) {
        return gson.fromJson(s, new TypeToken<List<Subtask>>() {
        });
    }


    //String --> Epic

    public Function<String, Epic> toEpicEntity() {
        return GsonMapper::applyToEpicEntity;
    }

    private static Epic applyToEpicEntity(String s) {
        return gson.fromJson(s, Epic.class);
    }

    //String --> List<Epic>

    public Function<String, List<Epic>> toListOfEpicsEntity() {
        return GsonMapper::applyToListOfEpics;
    }

    private static List<Epic> applyToListOfEpics(String s) {
        return gson.fromJson(s, new TypeToken<List<Epic>>() {
        });
    }

    //TreeSet<Task> --> String
    public Function<TreeSet<Task>, String> toJSONTreeSetOfTasks() {
        return GsonMapper::applyToJSONTreeSetOfTasks;
    }

    private static String applyToJSONTreeSetOfTasks(TreeSet<Task> tasks) {
        return gson.toJson(tasks);
    }

    //custom Serializer и Deserializer --> LocalDateTime для проекта

    private static class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {

        @Override
        public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(localDateTime.format(LDT_FORMATTER));
        }
    }

    private static class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {

        @Override
        public LocalDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return LocalDateTime.parse(jsonElement.getAsString(), LDT_FORMATTER);
        }
    }
}
