package com.ruoyi.server.mapper;

import com.ruoyi.server.domain.BscWithdrawalSign;
import org.apache.ibatis.annotations.Param;


import java.util.List;

public interface BscWithdrawalSignMapper {

    /**
     * 查询签名记录列表
     */
    List<BscWithdrawalSign> selectBscWithdrawalSignList(BscWithdrawalSign bscWithdrawalSign);

    /**
     * 根据ID查询签名记录
     */
    BscWithdrawalSign selectBscWithdrawalSignById(Integer id);

    /**
     * 根据提现日志ID和签名步骤查询
     */
    BscWithdrawalSign getByWithdrawLogIdAndStep(@Param("withdrawLogId") Integer withdrawLogId,
                                                @Param("signStep") Integer signStep);

    /**
     * 新增签名记录
     */
    int insert(BscWithdrawalSign bscWithdrawalSign);

    /**
     * 根据ID更新签名记录
     */
    int updateById(BscWithdrawalSign bscWithdrawalSign);

    /**
     * 根据提现日志ID和签名步骤更新（用于更新签名结果）
     */
    int updateByWithdrawLogIdAndStep(BscWithdrawalSign bscWithdrawalSign);

    /**
     * 统计已成功的签名记录数量
     * @param withdrawLogId 提现日志ID
     * @param signStep 签名步骤
     * @param signServer 签名服务器标识
     * @return 数量
     */
    long countSuccessSign(@Param("withdrawLogId") Long withdrawLogId,
                          @Param("signStep") Integer signStep,
                          @Param("signServer") String signServer);

    /**
     * 统计相同摘要已签名的记录数量
     * @param withdrawLogId 提现日志ID
     * @param signDigest 签名摘要
     * @return 数量
     */
    long countByWithdrawLogIdAndDigest(@Param("withdrawLogId") Long withdrawLogId,
                                       @Param("signDigest") String signDigest);
}