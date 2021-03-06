package topworker.dal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import topworker.dal.validation.ValidStartBeforeStop;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "WORK_PERIOD")
@ValidStartBeforeStop
public class WorkPeriod {

    @GeneratedValue
    @Id
    private long id;


    private Date start;

    private Date stop;

    @ManyToOne
    @JsonIgnore
    private WorkDay workDay;

    public WorkPeriod() {
    }


    public WorkPeriod(Date start, Date stop) {
        this.start = start;
        this.stop = stop;
    }

    public WorkDay getWorkDay() {
        return workDay;
    }

    public void setWorkDay(WorkDay workDay) {
        this.workDay = workDay;
    }

    public int getDuration() {
        return (int) ((stop.getTime() - start.getTime()) / 60000);
    }

    public long getId() {
        return id;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getStop() {
        return stop;
    }

    public void setStop(Date stop) {
        this.stop = stop;
    }


}
