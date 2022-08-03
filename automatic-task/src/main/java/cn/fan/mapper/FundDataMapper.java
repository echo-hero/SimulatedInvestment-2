package cn.fan.mapper;

import cn.fan.pojo.FundData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FundDataMapper {

    @Select(" select m.* from fund_data m,(select fundcode,max(funddate) rq from fund_data group by fundcode) maxm where m.fundcode=maxm.fundcode and m.funddate=maxm.rq ")
    List<FundData> findNewIndex();

    @Select(" select * from fund_data where fundcode = #{fundCode} and funddate = #{fundDate} ")
    FundData firstByFundcodeAndFunddate(String fundCode, String fundDate);

    @Select(" select * from fund_data where fundcode = #{fundCode} and funddate>= #{fundDate} order by funddate asc limit 0,1 ")
    FundData firstRecentlyByFundCodeAndFunddate(String fundCode,String fundDate);

}
