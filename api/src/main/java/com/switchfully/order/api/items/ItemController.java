package com.switchfully.order.api.items;

import com.switchfully.order.domain.items.Item;
import com.switchfully.order.domain.items.Item.StockUrgency;
import com.switchfully.order.domain.items.prices.Price;
import com.switchfully.order.service.items.ItemService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@CrossOrigin()
@RestController
@RequestMapping(path = "/" + ItemController.RESOURCE_NAME)
public class ItemController {

    public static final String RESOURCE_NAME = "items";

    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @Inject
    public ItemController(ItemService itemService, ItemMapper itemMapper) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
    }
    @CrossOrigin()
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemDto createItem(@RequestBody ItemDto itemDto) {
        return itemMapper.toDto(
                itemService.createItem(
                        itemMapper.toDomain(itemDto)));
    }
//    @CrossOrigin()
//    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ItemDto updateItem(@PathVariable String id, @RequestBody ItemDto itemDto) {
//        return itemMapper.toDto(
//                itemService.updateItem(
//                        itemMapper.toDomain(UUID.fromString(id), itemDto)));
//    }

    @CrossOrigin()
    @PutMapping(path = "/{name}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemDto updateItem(@PathVariable String name, @RequestBody ItemDto itemDto) {
        return itemMapper.toDto(
                itemService.updateItem(
                        itemMapper.toDomain(name, itemDto)));
    }

    @CrossOrigin()
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ItemDto> getAllItems(@RequestParam(name = "stockUrgency", required = false) String stockUrgency) {
        List<ItemDto> allItems = itemService.getAllItems().stream()
                .map(item -> itemMapper.toDto(item))
                .sorted(Comparator.comparingInt(ItemDto::getAmountOfStock))
                .collect(Collectors.toList());
        return filterOnStockUrgency(stockUrgency, allItems);
    }

    @CrossOrigin()
    @GetMapping(path = "/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemDto getItemByName(@PathVariable String name) {
        return itemService.getAllItems().stream()
                .map(item -> itemMapper.toDto(item))
                .filter(x -> x.getName().equals(name))
                .sorted(Comparator.comparingInt(ItemDto::getAmountOfStock))
                .collect(Collectors.toList()).get(0);
    }


    private List<ItemDto> filterOnStockUrgency(@RequestParam(name = "stockUrgency") String stockUrgency, List<ItemDto> allItems) {
        if(stockUrgency != null) {
            StockUrgency stockUrgencyToFilterOn = StockUrgency.valueOf(stockUrgency);
            return allItems.stream()
                    .filter(item -> StockUrgency.valueOf(item.getStockUrgency()).equals(stockUrgencyToFilterOn))
                    .collect(Collectors.toList());
        } else {
            return allItems;
        }
    }

}
