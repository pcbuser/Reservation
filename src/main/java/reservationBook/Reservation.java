package reservationBook;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import reservationBook.external.Book;
import reservationBook.external.BookService;

import java.util.Date;
import java.util.List;

@Entity
@Table(name="Reservation_table")
public class Reservation {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Integer bookId;
    private String reservationStatus;
    private Date reservationDate;
    private Date createDate;
    private String mType;

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    @PostPersist
    public void onPostPersist(){
        Reserved reserved = new Reserved();
        BeanUtils.copyProperties(this, reserved);
        reserved.publish();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        System.out.println("mType : "+mType);

        reservationBook.external.Book book = new reservationBook.external.Book();
        book.setId(Long.valueOf(this.getBookId()));
        if(mType.equals("reserved")){
            book.setBookStatus("02");
        }else{
            book.setBookStatus("01");
        }

        // mappings goes here
        Application.applicationContext.getBean(reservationBook.external.BookService.class)
            .bookStatusChange01(book);


    }

    @PostUpdate
    public void onPostUpdate(){

        ReservationCanceled reservationCanceled = new ReservationCanceled();
        BeanUtils.copyProperties(this, reservationCanceled);
        reservationCanceled.publish();

        Book book = new Book();

        book.setId(Long.valueOf(this.getBookId()));

        book.setBookStatus("01");

        BookService bookService = Application.applicationContext.getBean(BookService.class);
        bookService.bookStatusChange01(book);


    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }
    public String getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }
    public Date getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Date reservationDate) {
        this.reservationDate = reservationDate;
    }
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }




}
