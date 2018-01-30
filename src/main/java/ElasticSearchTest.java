import javafx.util.Pair;
import org.apache.http.HttpHost;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

public class ElasticSearchTest {

    private static final String INDEX = "elastic";
    private static final String TYPE = "test";
    private static final AtomicInteger id = new AtomicInteger(1);

    static void initData(RestHighLevelClient client) throws Exception{
//        questionIndex(client, new QuestionDto("This have, how to, i trying, help me, what is ",
//                "Just a couple of random phrases which may has been founded by the test query. Java enterprise."));
//        questionIndex(client, new QuestionDto("Question about java enterprise edition",
//                "I'm trying to understand java enterprise edition, but it is too hard without good books, can someone tell me some good books about java EE, please?"));
//        questionIndex(client, new QuestionDto("Witch programming language is better for enterprise: java or C#?",
//                "I want to became a enterprise developer, but i don't know witch language i must learn. I think that there is a two most popular languages: java and C#. Waiting for answers."));
//        questionIndex(client, new QuestionDto("Error in python program",
//                "I'm java programmer, and i'm trying to write easy python script but when i start it i get an error message: undefined symbol { in line 1" ));
//        questionIndex(client, new QuestionDto("JsonMappingException on android spring httprequest",
//                "I have this call in async task. All parameters are correct. In postman or advance rest client the call work fine and It return a json with a list of objects. But if I try to do this call in android with spring I have this error:"));
//        questionIndex(client, new QuestionDto("iOS automation with appium on Simulator",
//                "I am new into Appium iOS, Can someone give me the step by step installation process for Appium test on Mac OS using simulator. Note - I have installed Xcode, and have source file of app. Now which setting is required and which capabilities I need to add in code in Eclipse"));
//        questionIndex(client, new QuestionDto("how to restrict the date picker with min and max values on I phone using javascript language?",
//                "I have a scenario that,I am using a JavaScript function for min and max values for input type date.It is working fine on android mobile,but in I phone Am unable to restrict the calendar with max and min values."));
//        questionIndex(client, new QuestionDto("how to persist query result between objects in php",
//                "in my case , i have query result came from database : the result is a array, i want to pass result in multiple object and get result. i try to use mediator pattern but it wont work in my case , because i need to get result from first object and pass theme JsonMappingException to second and so on ."));
//        questionIndex(client, new QuestionDto("Prototype.js get 'Text' from an element. language javascript.",
//                "I'm new to Protoype.JS and just testing it a bit because I heard it was good, but I'm stuck quite quickly. As easy as this is with jQuery, it seems to be the end of the world to get the 'Text' in an element. I've tried innerHTML in multiple ways but the only thing I can get is 'undefined'."));
//        questionIndex(client, new QuestionDto("Elixir: pipe more then one variable into a function",
//                "Elixir has the possibility to pipe input into a function, which makes code more readable very often. For example something like this"));
    }

    static void testIndex(RestHighLevelClient client) throws Exception{
         IndexRequest request = new IndexRequest(INDEX, TYPE, String.valueOf(id.getAndIncrement()));
         request.source(new ObjectMapper().writeValueAsString(new UserDto(1, "name")), XContentType.JSON);
         IndexRequest request2 = new IndexRequest(INDEX, TYPE, String.valueOf(id.getAndIncrement()));
         request2.source(new ObjectMapper().writeValueAsString(new UserDto(2, "name2")), XContentType.JSON);
         System.out.println(client.index(request));
         System.out.println(client.index(request2));

    }

    static void questionIndex(RestHighLevelClient client, QuestionDto question) throws Exception{
        IndexRequest request = new  IndexRequest(INDEX, TYPE, String.valueOf(question.hashCode()));
        request.source(new ObjectMapper().writeValueAsString(question), XContentType.JSON);
        System.out.println(client.index(request));
    }

    static void testGetRequest(RestHighLevelClient client) throws Exception{
        GetRequest request = new GetRequest(INDEX, TYPE, "2");
        request.fetchSourceContext(new FetchSourceContext(
                true, new String[]{"name", "id"}, Strings.EMPTY_ARRAY));
        //request.storedFields("id");

        GetResponse response = client.get(request);
        System.out.println(response);

    }

    static void testSearch(RestHighLevelClient client, String query) throws Exception{
        SearchRequest request = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(createQuery(query));
        request.source(sourceBuilder);

        String[] result = client.search(request).toString().split("\"");
        for (int i = 2; i < result.length; i++) {
            if(result[i - 2].endsWith("title"))
                System.out.println("title: \"" + result[i] + "\"");
            else if(result[i - 2].endsWith("text"))
                System.out.println("text: \"" + result[i] + "\"\n");
        }
    }

    static BoolQueryBuilder createQuery(String searchString){
        QueryBuilder queryBuilder;

        if(!searchString.isEmpty()){
            queryBuilder = QueryBuilders.multiMatchQuery(searchString)
                    .field("title", 100)
                    .field("text", 10)
                    .fuzziness(Fuzziness.AUTO)
                    .type(MultiMatchQueryBuilder.Type.MOST_FIELDS)
                    .operator(Operator.OR);
        } else {
            queryBuilder = QueryBuilders.matchAllQuery();
        }
        return QueryBuilders.boolQuery().must(queryBuilder);
    }

    static void delete(RestHighLevelClient client) throws Exception{
//        DeleteRequest request = new DeleteRequest();
    }

    public static void main(String[] args) {
        try (RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http")))) {
            testSearch(client,"have this");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}