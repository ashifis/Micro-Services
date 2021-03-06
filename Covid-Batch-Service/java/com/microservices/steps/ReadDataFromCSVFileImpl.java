package com.microservices.steps;

import java.io.File;
import java.util.Arrays;
import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.stereotype.Component;

import com.microservices.listeners.CSVReaderStepListener;
import com.microservices.mappers.CovidDetailedDataMapper;
import com.microservices.models.CovidDetailedData;
import com.microservices.utils.LoadDataFromUri;


@Configuration
@ComponentScan(basePackages = "com.microservices")
@Component
//@Scope
public class ReadDataFromCSVFileImpl {
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	LoadDataFromUri loadDataFromUri;

	@Autowired
	private CSVReaderStepListener testMyStepListener;
	
	@Value("${csv_data_dir}")
	private String csv_data_dir;
	
	
	@Bean
	@StepScope
	public FlatFileItemReader<CovidDetailedData>  flatFileItemReader(){
		System.out.println("Enter flatFileItemReader");
		
		FlatFileItemReader<CovidDetailedData> fileItemReader  = new FlatFileItemReader<CovidDetailedData>();
		fileItemReader.setLinesToSkip(1);

		System.out.println("Dynamic File Name " + testMyStepListener.getCsvFileName());
		fileItemReader.setStrict(true);
		
		File directory = new File(csv_data_dir.concat("/".concat(testMyStepListener.getCsvFileName())));
		System.out.println(directory.getAbsolutePath());
		System.out.println("Exists " + directory.exists());
		
		fileItemReader.setResource(new PathResource(csv_data_dir.concat("/".concat(testMyStepListener.getCsvFileName()))));
		DefaultLineMapper<CovidDetailedData> defaultLineMapper = new DefaultLineMapper<CovidDetailedData>();
		
		DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer(",");
		delimitedLineTokenizer.setNames(testMyStepListener.getHeader());
		
		defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
		defaultLineMapper.setFieldSetMapper(new CovidDetailedDataMapper());
		defaultLineMapper.afterPropertiesSet();
		
		fileItemReader.setLineMapper(defaultLineMapper);
		System.out.println("Exit flatFileItemReader");
		return fileItemReader;
		
	}
	
	@Bean
	public org.springframework.batch.item.ItemWriter<CovidDetailedData> localItemWriter(){
		System.out.println("Enter localItemWriter");
		System.out.println("Exit localItemWriter");
		return items -> {
            for(CovidDetailedData item : items ){
               System.out.println("Actual " + item.toString());

            }
        };
	}


	
	
	@Bean
	public JdbcBatchItemWriter<CovidDetailedData> jdbcBatchCovidCountryWriter(){
		System.out.println("Enter jdbcBatchCovidCountryWriter");
		JdbcBatchItemWriter<CovidDetailedData> jdbcBatchItemWriter = new JdbcBatchItemWriter<CovidDetailedData>();
		jdbcBatchItemWriter.setDataSource(this.dataSource);
		
		jdbcBatchItemWriter.setSql(""
				+ "INSERT INTO COVID_COUNTRY_BATCH ( COUNTRY, PROVINCE, LONGITUDE, LATITUDE) "
				+ "VALUES ( :country, :province , :lat, :lon)"
				+ "");
		
		
	
		jdbcBatchItemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<CovidDetailedData>());
		jdbcBatchItemWriter.afterPropertiesSet();
			
		System.out.println("Exit jdbcBatchCovidCountryWriter");
		return jdbcBatchItemWriter;
		
	}
	
	@Bean
	public CompositeItemWriter<CovidDetailedData> compositeItemWriter(){
	    CompositeItemWriter<CovidDetailedData> writer = new CompositeItemWriter<CovidDetailedData>();
	    writer.setDelegates(Arrays.asList(localItemWriter(),jdbcBatchCovidCountryWriter()));
	    return writer;
	}
	
	
}
