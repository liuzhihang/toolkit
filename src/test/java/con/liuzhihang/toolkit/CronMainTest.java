package con.liuzhihang.toolkit;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

/**
 * @author liuzhihang
 * @date 2021/8/20 15:12
 */
public class CronMainTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        // validate();
        executeCron();
        // printDescription();
    }


    private static void validate() {
        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING);
        CronParser parser = new CronParser(cronDefinition);

        Cron cron = parser.parse("0/3 * * * * ?");
        cron.validate();
    }

    private static void executeCron() {

        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING);
        CronParser parser = new CronParser(cronDefinition);

        Cron cron = parser.parse("0 0/10 * * * ?");

        ExecutionTime executionTime = ExecutionTime.forCron(cron);

        ZonedDateTime startTime = ZonedDateTime.now();
        // 执行五次

        for (int i = 0; i < 5; i++) {
            Optional<ZonedDateTime> executeTimeOptional = executionTime.nextExecution(startTime);
            if (executeTimeOptional.isPresent()) {

                ZonedDateTime executeTime = executeTimeOptional.get();
                System.out.println(executeTime.format(FORMATTER));
                startTime = executeTime;
            }
        }


    }


    private static void printDescription() {

        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING);
        CronParser parser = new CronParser(cronDefinition);

        //create a descriptor for a specific Locale
        CronDescriptor descriptor = CronDescriptor.instance(Locale.ENGLISH);

        //parse some expression and ask descriptor for description
        String description = descriptor.describe(parser.parse("* 10 */12 * * ?"));
        System.out.println("description = " + description);
    }

}
