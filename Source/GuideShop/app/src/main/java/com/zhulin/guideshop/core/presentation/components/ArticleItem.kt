package com.zhulin.guideshop.core.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.zhulin.guideshop.R
import com.zhulin.guideshop.ui.theme.NewBlue
import com.zhulin.guideshop.ui.theme.NewWhite
import com.zhulin.guideshop.ui.theme.TextColor

@Composable
fun ArticleItem(
    onClick: () -> Unit,
    onAuthorClick: () -> Unit,
    onSubscribe: () -> Unit,
    onEdit: () -> Unit,
    onLike: () -> Unit,
    onComment: () -> Unit,
    onBookmark: () -> Unit,
    preview: String,
    authorAvatar: String,
    authorNickname: String,
    authorFollowers: String,
    showSubscribe: Boolean,
    subscribed: Boolean,
    logged: Boolean,
    userOwn: Boolean,
    createdAt: String,
    title: String,
    description: String,
    isLiked: Boolean,
    likeCount: String,
    commentCount: String,
    showAuthor: Boolean,
    bookmarked: Boolean
) {
    Box(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .clip(RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .heightIn(min = 200.dp, max = 350.dp)
            .background(TextColor)
            .clickable {
                onClick()
            }
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(preview)
                .crossfade(true)
                .build(),
            contentDescription = "Article Preview",
            modifier = Modifier
                .fillMaxWidth(),
            loading = {
                CircularProgressIndicator()
            },
            contentScale = ContentScale.Crop,
            alpha = 0.75f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(
                5.dp,
                Alignment.Top
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showAuthor) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            5.dp,
                            alignment = Alignment.Start
                        ),
                        modifier = Modifier.clickable {
                            onAuthorClick()
                        }
                    ) {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(authorAvatar)
                                .crossfade(true)
                                .transformations(
                                    CircleCropTransformation()
                                )
                                .build(),
                            contentDescription = "User avatar",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(TextColor)
                                .border(
                                    BorderStroke(2.dp, NewBlue),
                                    shape = CircleShape
                                ),
                            loading = {
                                CircularProgressIndicator()
                            }
                        )


                        Column {
                            Text(
                                text = authorNickname,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 16.sp
                                ),
                                color = NewWhite,
                            )

                            Text(
                                text = authorFollowers,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 12.sp
                                ),
                                color = NewWhite,
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        10.dp,
                        alignment = Alignment.End
                    )
                ) {
                    if (logged) {
                        IconButton(
                            onClick = { onBookmark() },
                            modifier = Modifier.size(24.dp),
                        ) {
                            Icon(
                                painter = if (bookmarked)
                                    painterResource(id = R.drawable.unbookmark_icon) else
                                    painterResource(id = R.drawable.bookmark_icon),
                                contentDescription = "Bookmark",
                                tint = NewWhite
                            )
                        }

                        if (showSubscribe) {
                            IconButton(
                                onClick = {
                                    onSubscribe()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    painter = if (subscribed) painterResource(id = R.drawable.unfollow_icon) else painterResource(
                                        id = R.drawable.follow_icon
                                    ),
                                    contentDescription = "Follow",
                                    tint = NewWhite,
                                )
                            }
                        }

                    } else if (userOwn) {
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Follow",
                                tint = NewWhite,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = createdAt,
                color = NewWhite,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 10.sp
                )
            )
            Text(
                text = title,
                color = NewWhite,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 24.sp
                )
            )
            Text(
                text = description,
                color = NewWhite,
                overflow = TextOverflow.Ellipsis,
                maxLines = 5,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    lineHeight = 18.sp
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    10.dp,
                    Alignment.Start
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = { onLike() },
                    modifier = Modifier.size(24.dp),
                ) {
                    Icon(
                        painter = if (isLiked) painterResource(id = R.drawable.unlike_icon) else painterResource(
                            id = R.drawable.like_icon
                        ),
                        contentDescription = "Bookmark",
                        tint = NewWhite,
                    )
                }
                Text(
                    text = likeCount,
                    color = NewWhite,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 10.sp
                    )
                )
                IconButton(
                    onClick = { onComment() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.comment_icon),
                        contentDescription = "Comment",
                        tint = NewWhite,
                    )
                }

                Text(
                    text = commentCount,
                    color = NewWhite,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 10.sp
                    )
                )
            }

        }
    }
}

