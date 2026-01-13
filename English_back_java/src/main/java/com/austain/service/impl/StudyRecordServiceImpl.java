package com.austain.service.impl;

import com.austain.domain.dto.RecordResult;
import com.austain.domain.po.RecordRequest;
import com.austain.mapper.StudyRecordMapper;
import com.austain.service.StudyRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudyRecordServiceImpl implements StudyRecordService {

    @Autowired
    private StudyRecordMapper studyRecordMapper;

    /**
     * 添加学习记录
     * @param recordRequest 学习记录
     * @return 添加成功返回1，添加失败返回0
     */
    @Override
    @Transactional
    public int addStudyRecord(RecordRequest recordRequest) {
        if (recordRequest.getSelected() == 0) {
            recordRequest.setSelected(0);
        }
        if (recordRequest.getAlreadyReviewed() == 0) {
            recordRequest.setAlreadyReviewed(0);
        }
        studyRecordMapper.setSelected(recordRequest.getUserId());
        return studyRecordMapper.addStudyRecord(recordRequest);
    }

    /**
     * 获取今天学习记录  -- 根据艾宾浩斯遗忘曲线规律返回
     * @param userId 用户ID
     * @return 今日学习记录
     */
    @Override
    @Transactional  // 添加事务
    public List<RecordResult> getTodayList(int userId, int type) {

        // TODO 由前端统一计算距今天数
        List<RecordResult> recordResultList = new ArrayList<>();  // 返回结果
        List<Integer> ids = new ArrayList<>();  // 定义数组用来存放要标记的记录id，一次性更新

        // 第一次从数据库查询后做标记，今天之内就不做更改，只要数据库有标记即返回不为空，只从数据库查，不再走下方代码计算
        List<RecordResult> selectedList = studyRecordMapper.getTodayListSelected(userId, type);
        if (selectedList != null && !selectedList.isEmpty()) {
            return selectedList;
        }

        // 数据库返回为空，说明为当天第一天查询
        // 需要先从数据库查询用户的所有学习记录
        List<RecordResult> studyRecordListALL = studyRecordMapper.getListAll(userId, type);  // 按时间降序

        // 空校验
        if (studyRecordListALL == null || studyRecordListALL.isEmpty()) {
            return recordResultList;  // 返回空列表
        }



        // 对用户的所有学习记录进行筛选，按照艾宾浩斯遗忘曲线返回今天学习记录
        LocalDate now = LocalDate.now();
        for (RecordResult recordResult : studyRecordListALL) {
            // 更准确的计算方式
            LocalDate createTimeDate = recordResult.getCreateTime().toLocalDate();
            long toDate = Math.abs(ChronoUnit.DAYS.between(createTimeDate, now));

            switch ((int)toDate){  // 将long强制转换成int，此处表示的天数，int范围足够用，不会溢出
                case 0:
                case 1:
                case 2:
                case 4:
                case 7:
                case 15:
                case 30:
                    recordResultList.add(recordResult);    // 添加到返回结果中
                    ids.add(recordResult.getId());  // 添加到id列表中，后续批量更新
                    break;
                default:
                    break;
            }
        }
        // 将筛选出来的数据在数据库做标记，下次查询时从数据库查询该标记
        if (!ids.isEmpty()){
            studyRecordMapper.updateSelected(ids);
        }

        // 定义定时器，每晚00点重置数据库标记
        return recordResultList;
    }

    /**
     * （批量）删除学习记录
     * @param ids 需要删除的记录ID列表
     * @param userId 用户ID
     * @return 删除成功返回受影响行数，失败返回0
     */
    @Override
    @Transactional
    public int deleteRecords(List<String> ids, int userId) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return studyRecordMapper.deleteRecords(ids, userId);
    }

    /**
     * 更新学习记录
     * @param request 前端传递的参数——请求体
     * @return 更新成功返回1，失败返回0
     */
    @Override
    public int updateRecord(RecordRequest request) {
        return studyRecordMapper.updateRecord(request);
    }

    @Override
    public int setReview(int userId, List<Integer> ids) {
        studyRecordMapper.resetReviewed(userId);  // 根据用户id重置所有已复习，先全部清空
        if (ids == null || ids.isEmpty()){
            return 1;
        }
        return studyRecordMapper.setReview(ids);  // 批量更新已复习
    }

    @Override
    public int resetSelected(int userId) {
        return studyRecordMapper.resetSelectByUserId(userId);
    }
}
