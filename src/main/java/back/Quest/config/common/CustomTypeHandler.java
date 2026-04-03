package back.Quest.config.common;

import back.Quest.model.Enum.EnumClass;
import back.Quest.model.dto.missing.AreaCode;
import back.Quest.model.dto.missing.MissingStatus;
import back.Quest.model.dto.quiz.ValidationStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

@MappedTypes({AreaCode.class, MissingStatus.class, ValidationStatus.class})
public class CustomTypeHandler<A extends Enum<A> & EnumClass> extends BaseTypeHandler<A> {

    private final Class<A> type;

    public CustomTypeHandler(Class<A> type) {
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, A parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getCode());
    }

    @Override
    public A getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return fromCode(rs.getString(columnName));
    }

    @Override
    public A getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return fromCode(rs.getString(columnIndex));
    }

    @Override
    public A getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return fromCode(cs.getString(columnIndex));
    }

    private A fromCode(String code) {
        if (code == null) return null;
        return Arrays.stream(type.getEnumConstants())
                .filter(v -> v.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown code: " + code));
    }
}