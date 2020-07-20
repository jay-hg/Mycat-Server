package study.parser;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class ParserMain {
    public static void main(String[] args) {
        String sql = "select id,name from user where createTime >= '2019-07-15 12:00:00' and createTime <= '2020-07-15 12:00:00'";

        SQLStatementParser sqlStatementParser = new MySqlStatementParser(sql);

        SQLStatement sqlStatement = sqlStatementParser.parseStatement();

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        sqlStatement.accept(visitor);

        visitor.getTables().forEach((k,v)->{
            System.out.println(k+","+v);
        });

        visitor.getConditions().forEach(condition -> {
            System.out.println(condition.getColumn()+","+condition.getOperator()+","+condition.getValues().get(0));
        });

    }
}
