package org.zerock.b01.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Reply;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.ReplyDTO;
import org.zerock.b01.repository.ReplyRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Log4j2
public class ReplyServiceImpl implements ReplyService{

    private final ReplyRepository replyRepository;

    private final ModelMapper modelMapper;

    @Override
    public Long register(ReplyDTO replyDTO) {
        // 댓글 등록 ReplyDTO를 받아 save 처리후 rno를 리턴한다.
        Reply reply = modelMapper.map(replyDTO, Reply.class);
        Long rno = replyRepository.save(reply).getRno();
        System.out.println(replyDTO.getRno());
        return rno;
    }

    @Override
    public ReplyDTO read(Long rno) {
        // rno를 받아 findById 찾아와 Reply 객체를 모델로 리턴한다.
        Optional<Reply> replyOptional = replyRepository.findById(rno);
        Reply reply = replyOptional.orElseThrow();

        return modelMapper.map(reply, ReplyDTO.class);
    }

    @Override
    public void modify(ReplyDTO replyDTO) {
        // ReplyDTO를 받아 findById로 rno로 찾고온 객체가 있으면 Reply 객체에 changeText를 이용하여 댓글내용을 수정하고 save update 한다.
        Optional<Reply> replyOptional = replyRepository.findById(replyDTO.getRno());

        Reply reply = replyOptional.orElseThrow();

        reply.changeText(replyDTO.getReplyText());

        replyRepository.save(reply);

    }

    @Override
    public void remove(Long rno) {
        // rno 댓글 번호를 이용하여 찾아 deleteById로 객체를 삭제한다.
        replyRepository.deleteById(rno);

    }

    @Override
    public PageResponseDTO<ReplyDTO> getListOfBoard(Long bno, PageRequestDTO pageRequestDTO) {
        // 댓글 리스트 처리용 -> bno를 활용하여 페이징 처리를 추가한다.
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() <=0? 0: pageRequestDTO.getPage() -1,
                pageRequestDTO.getSize(),
                Sort.by("rno").ascending());

        Page<Reply> result = replyRepository.listOfBoard(bno, pageable);

        List<ReplyDTO> dtoList =
                result.getContent().stream().map(reply -> modelMapper.map(reply, ReplyDTO.class))
                        .collect(Collectors.toList());
        // 실제 반환 되어야 하는 타입이 Reply가 아니라 ReplyDTO임으로 변환 작업 진행

        return PageResponseDTO.<ReplyDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();

    }
}
