package com.morningstar.common.dao.type;

import com.morningstar.util.ByteUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class UUIDTypeHandler extends BaseTypeHandler<UUID> {
    private UUID parse(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(ByteUtil.byteToHexString(aByte));
        }
        sb.insert(8, '-');
        sb.insert(13, '-');
        sb.insert(18, '-');
        sb.insert(23, '-');
        return UUID.fromString(sb.toString());
    }

    private byte[] format(UUID uuid) {
        String data = uuid.toString().replace("-", "");
        byte[] bytes = new byte[16];
        for (int i = 0; i < bytes.length; i++) {
            int digit1 = Character.digit(data.charAt(2 * i), 16);
            int digit2 = Character.digit(data.charAt(2 * i + 1), 16);
            bytes[i] = (byte) (digit1 * 16 + digit2);
        }
        return bytes;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UUID parameter, JdbcType jdbcType) throws SQLException {
        ps.setBytes(i, format(parameter));
    }

    @Override
    public UUID getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getBytes(columnName));
    }

    @Override
    public UUID getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getBytes(columnIndex));
    }

    @Override
    public UUID getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getBytes(columnIndex));
    }
}
